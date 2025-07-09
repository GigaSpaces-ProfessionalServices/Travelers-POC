package com.consumer;

import com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.SpaceProxyConfigurer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class ConsumerPostgres {
    private static volatile boolean running = true;
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private GigaSpace gigaSpace;
    private Map<String, List<String>> typeAndKeyMap;

    public static void main(String[] args) {
        ConsumerPostgres consumer = new ConsumerPostgres();
        consumer.setGiGaspaceConfig();
        consumer.setTypesAndKeys();
        consumer.kafkaV2();
    }

    public void setGiGaspaceConfig() {
        String locators = "xap-manager-service";
        String spaceName = "space";
        String lookupGroup = "xap-17.0.1";
        gigaSpace = new GigaSpaceConfigurer(new SpaceProxyConfigurer(spaceName)
                .lookupGroups(lookupGroup)
                .lookupLocators(locators))
                .gigaSpace();
        log.info("gigaSpace is set " + gigaSpace);
    }

    public void setTypesAndKeys() {
        typeAndKeyMap = GigaspaceUtil.getObjectInfo();
        log.info("typeAndKeyMap is set " + typeAndKeyMap);
    }

    public void kafkaV2() {
        String bootstrapServers = "kafka:9092";
        String topicName = "dih-write-back";

        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "dih-write-back"); // Change to force fresh offset
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "com.consumer.KafkaMessageDeserializer"); // org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest"); // Read from beginning
        props.put("enable.auto.commit", "false"); // Read from beginning
        log.info("Using bootstrap.servers: " + bootstrapServers);
        KafkaConsumer<String, KafkaMessage> consumer = new KafkaConsumer<>(props);
        try (consumer) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("\nShutdown signal received. Closing Kafka consumer...");
                running = false;
                consumer.wakeup(); // Causes poll() to exit immediately
            }));
            consumer.subscribe(Collections.singletonList(topicName));

            log.info("Listening for messages on topic: " + topicName);
            long lastLogTime = System.currentTimeMillis();
            while (running) {
                ConsumerRecords<String, KafkaMessage> records = consumer.poll(Duration.ofSeconds(1));
                boolean shouldLog = records.count() > 0 ||
                        (System.currentTimeMillis() - lastLogTime) >= 30_000; // 30 seconds
                if (shouldLog) {
                    log.info("Received records count: " + records.count()); //records.count()
                    lastLogTime = System.currentTimeMillis(); // reset timer
                }
                for (ConsumerRecord<String, KafkaMessage> record : records) {
                    log.info("Consuming Kafka message value=" + record.value() + ", partition=" + record.partition() + ", offset=" + record.offset() + ", key=" + record.key());
                    KafkaMessage kafkaMessage = record.value();
                    log.info("Consuming Kafka message " + kafkaMessage);
                    String operationType = kafkaMessage.getDataOperationType().name();
                    if (kafkaMessage.hasDataAsMap()) {
                        log.info("Consuming Kafka message with operation type " + operationType + " and data " + kafkaMessage.getDataAsMap());
                        //Map<String, Object> dataAsMap = ;
                        Map<String, Object> dataAsMap = new HashMap<>(kafkaMessage.getDataAsMap());
                        log.info("dataAsMap : "+dataAsMap);
                        dataAsMap.remove("ZZ_META_DI_TIMESTAMP");
                        log.info("removed ZZ_META_DI_TIMESTAMP ::" + dataAsMap);
                        //if ("WRITE".equals(operationType) || "UPDATE".equals(operationType)) {
                        performPostgresOperation(operationType, dataAsMap, kafkaMessage.getTypeName()); // Handles dynamic fields
                        //}
                    }
                }
            }
        } catch (WakeupException e) {
            if (running) throw e; // Unexpected, rethrow
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            log.info("Kafka consumer closed cleanly.");
        }
    }

    public void performPostgresOperation(String operationType, Map<String, Object> dataMap, String typeName) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://postgres:5432/mydb", "myuser", "password123")) {
            log.info("data: " + dataMap);
            if (!typeAndKeyMap.containsKey(typeName)) {
                log.info("Refreshing typeAndKeyMap");
                setTypesAndKeys();
            }
            // 1. Create table if not exists
            if (!tableExists(conn, typeName)) {
                log.info("Creating table " + typeName);
                createTable(conn, typeName, dataMap);
            }
            if ("WRITE".equals(operationType)) {
                log.info("inserting data: ");
                // 2. Insert row
                insertRow(conn, typeName, dataMap);
                log.info("Inserted data");
            } else if ("UPDATE".equals(operationType)) {
                log.info("updating data: ");
                // 2. Update row
                updateRow(conn, typeName, dataMap);
                log.info("Updated data");
            } else if ("REMOVE".equals(operationType)) {
                log.info("deleting data: ");
                // 2. REMOVE row
                deleteRow(conn, typeName, dataMap);
                log.info("Deleted data");
            } else {
                log.info("Unknown operation type: " + operationType);
            }
            //conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            log.info("ERROR :: Failed to insert data: " + e.getMessage());
        }
    }

    private boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName.toLowerCase(), null)) {
            return rs.next();
        }
    }

    private void createTable(Connection conn, String tableName, Map<String, Object> dataMap) throws SQLException {
        List<String> primaryKeys = typeAndKeyMap.get(tableName); // e.g., ["ID", "CREATEDDATE"]
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");

        for (String key : dataMap.keySet()) {
            sb.append(key).append(" TEXT, "); // All columns as TEXT (can enhance to infer types)
        }

        // Add PRIMARY KEY clause if keys exist
        if (primaryKeys != null && !primaryKeys.isEmpty()) {
            sb.append("PRIMARY KEY (");
            for (String pk : primaryKeys) {
                sb.append(pk).append(", ");
            }
            sb.setLength(sb.length() - 2); // Remove last comma
            sb.append(")");
        } else {
            sb.setLength(sb.length() - 2); // Remove last comma from column list
        }

        sb.append(")");

        try (Statement stmt = conn.createStatement()) {
            log.info("Creating table SQL: " + sb.toString());
            stmt.executeUpdate(sb.toString());
            log.info("Table created: " + tableName);
        }
    }


    private void insertRow(Connection conn, String tableName, Map<String, Object> dataMap) throws SQLException {
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (String key : dataMap.keySet()) {
            columns.append(key).append(", ");
            placeholders.append("?, ");
        }

        columns.setLength(columns.length() - 2);
        placeholders.setLength(placeholders.length() - 2);

        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
        log.info("Inserting row SQL: " + sql);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int index = 1;
            for (String key : dataMap.keySet()) {
                pstmt.setString(index++, dataMap.get(key) != null ? dataMap.get(key).toString() : null);
            }
            pstmt.executeUpdate();
        }
    }

    private void updateRow(Connection conn, String tableName, Map<String, Object> dataMap) throws SQLException {
        List<String> primaryKeys = typeAndKeyMap.get(tableName);
        if (primaryKeys == null || primaryKeys.isEmpty()) {
            throw new IllegalArgumentException("No primary keys configured for table: " + tableName);
        }

        StringBuilder setClause = new StringBuilder();
        StringBuilder whereClause = new StringBuilder();

        for (String key : dataMap.keySet()) {
            if (!primaryKeys.contains(key)) {
                setClause.append(key).append(" = ?, ");
            }
        }

        for (String pk : primaryKeys) {
            whereClause.append(pk).append(" = ? AND ");
        }

        if (setClause.length() == 0) {
            throw new IllegalArgumentException("No non-key fields to update.");
        }

        setClause.setLength(setClause.length() - 2);
        whereClause.setLength(whereClause.length() - 5); // Remove last AND

        String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE " + whereClause;
        log.info("Updating row SQL: " + sql);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int index = 1;

            // Set non-PK values
            for (String key : dataMap.keySet()) {
                if (!primaryKeys.contains(key)) {
                    pstmt.setString(index++, dataMap.get(key) != null ? dataMap.get(key).toString() : null);
                }
            }

            // Set PK values
            for (String pk : primaryKeys) {
                pstmt.setString(index++, dataMap.get(pk) != null ? dataMap.get(pk).toString() : null);
            }

            pstmt.executeUpdate();
        }
    }

    private void deleteRow(Connection conn, String tableName, Map<String, Object> dataMap) throws SQLException {
        List<String> primaryKeys = typeAndKeyMap.get(tableName);
        if (primaryKeys == null || primaryKeys.isEmpty()) {
            throw new IllegalArgumentException("No primary keys configured for table: " + tableName);
        }

        StringBuilder whereClause = new StringBuilder();
        for (String pk : primaryKeys) {
            whereClause.append(pk).append(" = ? AND ");
        }
        whereClause.setLength(whereClause.length() - 5); // Remove last AND

        String sql = "DELETE FROM " + tableName + " WHERE " + whereClause;
        log.info("Deleting row SQL: " + sql);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int index = 1;
            for (String pk : primaryKeys) {
                pstmt.setString(index++, dataMap.get(pk) != null ? dataMap.get(pk).toString() : null);
            }
            pstmt.executeUpdate();
        }
    }

}

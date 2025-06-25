package com.epam.openspaces.persistency.kafka;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;

import org.apache.commons.io.FileUtils;

/**
 * @author Oleksiy_Dyagilev
 */
public class EmbeddedKafka {

    private int kafkaPort;
    private int zookeperPort;
    private KafkaServerStartable kafka;
    private File logDir;

    public EmbeddedKafka(int kafkaPort, int zookeperPort) {
        this.kafkaPort = kafkaPort;
        this.zookeperPort = zookeperPort;
    }

    public void startup() {
        logDir = TestUtils.tempDir("kafkaLogDir");


        try {
            FileUtils.deleteDirectory(logDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete temp dirs", e);
        }

        Properties props = new Properties();
        props.setProperty("zookeeper.connect", "localhost:" + zookeperPort);
        props.setProperty("port", String.valueOf(kafkaPort));
        props.setProperty("broker.id", "0");
        props.setProperty("num.partitions", "1");
        props.setProperty("log.dirs", logDir.getAbsolutePath());

        KafkaConfig kafkaConfig = new KafkaConfig(props);
        kafka = new KafkaServerStartable(kafkaConfig);
        kafka.startup();
    }

    public void shutdown() {
        kafka.shutdown();
    }
}

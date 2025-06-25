package com.epam.openspaces.persistency.kafka;

import com.epam.openspaces.persistency.kafka.protocol.AbstractKafkaMessage;
import com.epam.openspaces.persistency.kafka.protocol.AbstractKafkaMessageFactory;
import com.epam.openspaces.persistency.kafka.protocol.AbstractKafkaMessageKey;
import com.gigaspaces.sync.DataSyncOperation;
import com.gigaspaces.sync.OperationsBatchData;
import com.gigaspaces.sync.SpaceSynchronizationEndpoint;
import com.gigaspaces.sync.TransactionData;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Incomplete implementation of Space Synchronization Endpoint which uses Apache Kafka as external data store.
 * Space synchronization operations are converted to XAP-Kafka protocol and sent to Kafka server.
 *
 * @author Oleksiy_Dyagilev
 */
public abstract class AbstractKafkaSpaceSynchronizationEndpoint
        <K extends AbstractKafkaMessageKey, M extends AbstractKafkaMessage> extends SpaceSynchronizationEndpoint {

    protected Log logger = LogFactory.getLog(this.getClass().getName());

    protected AbstractKafkaMessageFactory<K, M> kafkaMessageFactory;
    protected Producer<K, M> kafkaProducer;

    /**
     * resolves Kafka topic for given message
     */
    protected abstract String resolveTopicForMessage(M message);

    @Override
    public void onTransactionSynchronization(TransactionData transactionData) {
        executeDataSyncOperations(transactionData.getTransactionParticipantDataItems());
    }

    @Override
    public void onOperationsBatchSynchronization(OperationsBatchData batchData) {
        executeDataSyncOperations(batchData.getBatchDataItems());
    }

    protected void executeDataSyncOperations(DataSyncOperation[] transactionParticipantDataItems) {
        sendToKafka(transactionParticipantDataItems);
    }

    /**
     * converts XAP data sync operations to Kafka messages (protocol objects) and sends them to Kafka server
     */
    protected void sendToKafka(DataSyncOperation[] dataSyncOperations) {
        List<KeyedMessage<K, M>> kafkaMessages = new ArrayList<KeyedMessage<K, M>>(dataSyncOperations.length);

        for (DataSyncOperation dataSyncOperation : dataSyncOperations) {
            try {
                M message = kafkaMessageFactory.createMessage(dataSyncOperation);
                K key = kafkaMessageFactory.createMessageKey(dataSyncOperation);

                String topic = resolveTopicForMessage(message);

                if (StringUtils.isEmpty(topic)) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Topic for message not found. Message will be filtered out. " + message);
                    }
                } else {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Writing to Kafka " + message);
                    }

                    kafkaMessages.add(new KeyedMessage<K, M>(topic, key, message));
                }

            } catch (KafkaPersistenceException e) {
                logger.error("Exception during Kafka protocol object creation. This data operation will not be persisted", e);
            }
        }

        kafkaProducer.send(kafkaMessages);
    }

    public void setKafkaMessageFactory(AbstractKafkaMessageFactory<K, M> kafkaMessageFactory) {
        this.kafkaMessageFactory = kafkaMessageFactory;
    }
}

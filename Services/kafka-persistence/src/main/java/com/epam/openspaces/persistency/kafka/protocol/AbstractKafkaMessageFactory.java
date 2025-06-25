package com.epam.openspaces.persistency.kafka.protocol;

import com.epam.openspaces.persistency.kafka.KafkaPersistenceException;
import com.gigaspaces.sync.DataSyncOperation;

/**
 * Factory of Kafka messages
 *
 * @author Oleksiy_Dyagilev
 */
public interface AbstractKafkaMessageFactory<K extends AbstractKafkaMessageKey, M extends AbstractKafkaMessage> {

    /**
     * Creates Kafka message from XAP data sync operation.
     */
    public M createMessage(DataSyncOperation syncOperation) throws KafkaPersistenceException;

    /**
     * Creates Kafka message key from XAP data sync operation.
     */
    public K createMessageKey(DataSyncOperation syncOperation) throws KafkaPersistenceException;

}

package com.epam.openspaces.persistency.kafka.protocol.impl;

import com.epam.openspaces.persistency.kafka.KafkaPersistenceException;
import com.epam.openspaces.persistency.kafka.protocol.AbstractKafkaMessageFactory;
import com.gigaspaces.sync.DataSyncOperation;
import com.gigaspaces.sync.DataSyncOperationType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory of Kafka default protocol objects
 *
 * @author Oleksiy_Dyagilev
 */
public class KafkaMessageFactory implements AbstractKafkaMessageFactory<KafkaMessageKey, KafkaMessage> {

    // mapping from XAP operation type to Kafka message operation type
    private static final Map<DataSyncOperationType, KafkaDataOperationType> typesMap = new HashMap<DataSyncOperationType, KafkaDataOperationType>();
    static {
        typesMap.put(DataSyncOperationType.WRITE, KafkaDataOperationType.WRITE);
        typesMap.put(DataSyncOperationType.CHANGE, KafkaDataOperationType.CHANGE);
        typesMap.put(DataSyncOperationType.PARTIAL_UPDATE, KafkaDataOperationType.PARTIAL_UPDATE);
        typesMap.put(DataSyncOperationType.REMOVE, KafkaDataOperationType.REMOVE);
        typesMap.put(DataSyncOperationType.REMOVE_BY_UID, KafkaDataOperationType.REMOVE_BY_UID);
        typesMap.put(DataSyncOperationType.UPDATE, KafkaDataOperationType.UPDATE);
    }

    /**
     * Creates Kafka message from XAP data sync operation.
     * The underlying object of data sync operation must be Serializable,
     * otherwise KafkaPersistenceException is thrown.
     *
     * @throws com.epam.openspaces.persistency.kafka.KafkaPersistenceException if underlying object of data sync operation is not Serializable.
     */
    @Override
    public KafkaMessage createMessage(DataSyncOperation syncOperation) throws KafkaPersistenceException {
        KafkaDataOperationType type = typesMap.get(syncOperation.getDataSyncOperationType());

        if (syncOperation.supportsDataAsObject()) {
            Object object = syncOperation.getDataAsObject();
            if (object instanceof Serializable) {
                return new KafkaMessage(type, (Serializable) object);
            } else {
                throw new KafkaPersistenceException("Not serializable object of DataSyncOperation " + syncOperation);
            }
        } else if (syncOperation.supportsDataAsDocument()) {
            return new KafkaMessage(type, syncOperation.getDataAsDocument().getProperties());
        } else {
            throw new KafkaPersistenceException("Unable to convert DataSyncOperation to Kafka protocol. " +
                    "DataSyncOperation = " + syncOperation);
        }
    }


    /**
     * Creates uniform distributed Message Key.
     */
    @Override
    public KafkaMessageKey createMessageKey(DataSyncOperation syncOperation) throws KafkaPersistenceException {
        return new KafkaMessageKey(System.identityHashCode(syncOperation));
    }

}

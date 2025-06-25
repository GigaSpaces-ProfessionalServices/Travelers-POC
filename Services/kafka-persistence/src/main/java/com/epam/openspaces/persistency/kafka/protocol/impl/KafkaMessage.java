package com.epam.openspaces.persistency.kafka.protocol.impl;

import com.epam.openspaces.persistency.kafka.protocol.AbstractKafkaMessage;

import java.io.Serializable;
import java.util.Map;

/**
 * Message of default XAP-Kafka protocol.
 *
 * Represents XAP data operation. Consists of the operation type and data.
 * Data itself could be represented either as a single object or as a dictionary of key/values.
 * 
 * @see com.epam.openspaces.persistency.kafka.protocol.impl.serializer.KafkaMessageEncoder
 * @see com.epam.openspaces.persistency.kafka.protocol.impl.serializer.KafkaMessageDecoder
 * 
 * @author Oleksiy_Dyagilev
 */
public class KafkaMessage implements AbstractKafkaMessage, Serializable {

    private KafkaDataOperationType dataOperationType;

    private Serializable dataAsObject;
    private Map<String, Object> dataAsMap;

    public KafkaMessage(KafkaDataOperationType dataOperationType,
                        Serializable dataAsObject) {
        this.dataOperationType = dataOperationType;
        this.dataAsObject = dataAsObject;
    }

    public KafkaMessage(KafkaDataOperationType dataOperationType,
                        Map<String, Object> dataAsMap) {
        this.dataOperationType = dataOperationType;
        this.dataAsMap = dataAsMap;
    }

    /**
     * @return operation type
     */
    public KafkaDataOperationType getDataOperationType() {
        return dataOperationType;
    }

    /**
     * @return data as object. May return null if not supported. Call hasDataAsObject() to check if supported.
     */
    public Serializable getDataAsObject() {
        return dataAsObject;
    }

    /**
     * @return data as a dictionary. May return null if not supported. Call hasDataAsMap() to check if supported.
     */
    public Map<String, Object> getDataAsMap() {
        return dataAsMap;
    }

    /**
     * @return true if data represents object
     */
    public boolean hasDataAsObject() {
        return dataAsObject != null;
    }

    /**
     * @return true if data represents dictionary
     */
    public boolean hasDataAsMap() {
        return dataAsMap != null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((dataAsMap == null) ? 0 : dataAsMap.hashCode());
        result = prime * result
                + ((dataAsObject == null) ? 0 : dataAsObject.hashCode());
        result = prime
                * result
                + ((dataOperationType == null) ? 0 : dataOperationType
                        .hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        KafkaMessage other = (KafkaMessage) obj;
        if (dataAsMap == null) {
            if (other.dataAsMap != null) {
                return false;
            }
        } else if (!dataAsMap.equals(other.dataAsMap)) {
            return false;
        }
        if (dataAsObject == null) {
            if (other.dataAsObject != null) {
                return false;
            }
        } else if (!dataAsObject.equals(other.dataAsObject)) {
            return false;
        }
        if (dataOperationType != other.dataOperationType) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "KafkaMessage{" + "dataOperationType=" + dataOperationType
                + ", dataAsObject=" + dataAsObject + ", dataAsMap=" + dataAsMap
                + '}';
    }
}

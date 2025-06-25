package com.epam.openspaces.persistency.kafka.protocol.impl;

/**
 * Represents data operation type as a part of XAP-Kafka message protocol.
 *
 * @see KafkaMessage
 * @author Oleksiy_Dyagilev
 */
public enum KafkaDataOperationType {
    WRITE, UPDATE, PARTIAL_UPDATE, REMOVE, REMOVE_BY_UID, CHANGE
}

package com.epam.openspaces.persistency.kafka.protocol.impl;

import com.epam.openspaces.persistency.kafka.protocol.AbstractKafkaMessageKey;

import java.io.Serializable;

/**
 * Message Key of default XAP-Kafka protocol.
 *
 * NOTE: underlying integer value is NOT UNIQUE.
 *
 * @author Oleksiy_Dyagilev
 */
public class KafkaMessageKey implements AbstractKafkaMessageKey, Serializable {
    private int value;

    public KafkaMessageKey() {
    }

    public KafkaMessageKey(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

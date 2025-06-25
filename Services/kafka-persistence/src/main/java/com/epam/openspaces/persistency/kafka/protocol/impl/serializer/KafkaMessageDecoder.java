package com.epam.openspaces.persistency.kafka.protocol.impl.serializer;

import com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessage;
import kafka.serializer.Decoder;

import org.apache.commons.lang.SerializationUtils;

/**
 * Decoder for XAP-Kafka protocol.
 *
 * @see com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessage
 */
public class KafkaMessageDecoder implements Decoder<KafkaMessage> {

    @Override
    public KafkaMessage fromBytes(byte[] bytes) {
        return (KafkaMessage) SerializationUtils.deserialize(bytes);
    }
}

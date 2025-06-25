package com.epam.openspaces.persistency.kafka.protocol.impl.serializer;

import com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessageKey;
import kafka.serializer.Decoder;
import org.apache.commons.lang.SerializationUtils;

/**
 * Decoder for XAP-Kafka protocol.
 *
 * @author Oleksiy_Dyagilev
 */
public class KafkaMessageKeyDecoder implements Decoder<KafkaMessageKey> {

    @Override
    public KafkaMessageKey fromBytes(byte[] bytes) {
        return (KafkaMessageKey) SerializationUtils.deserialize(bytes);
    }
}

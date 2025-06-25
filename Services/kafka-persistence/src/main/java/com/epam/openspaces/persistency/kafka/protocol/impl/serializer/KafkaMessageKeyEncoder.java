package com.epam.openspaces.persistency.kafka.protocol.impl.serializer;

import com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessageKey;
import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;
import org.apache.commons.lang.SerializationUtils;

/**
 * Encoder for XAP-Kafka protocol.
 *
 * @author Oleksiy_Dyagilev
 */
public class KafkaMessageKeyEncoder implements Encoder<KafkaMessageKey> {

    public KafkaMessageKeyEncoder(VerifiableProperties props) {
        // this constructor is used by Kafka to create an instance of Encoder classes via reflection
    }

    @Override
    public byte[] toBytes(KafkaMessageKey kafkaMessageKey) {
        return SerializationUtils.serialize(kafkaMessageKey);
    }
}

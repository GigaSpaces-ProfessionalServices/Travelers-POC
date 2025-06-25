package com.epam.openspaces.persistency.kafka.protocol.impl.serializer;

import com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessage;
import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;
import org.apache.commons.lang.SerializationUtils;

/**
 * Encoder for XAP-Kafka protocol.
 *
 * @see com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessage
 *
 * @author Oleksiy_Dyagilev
 */
public class KafkaMessageEncoder implements Encoder<KafkaMessage> {

    public KafkaMessageEncoder(VerifiableProperties props) {
        // this constructor is used by Kafka to create an instance of Encoder classes via reflection
    }

    @Override
    public byte[] toBytes(KafkaMessage message) {
        return SerializationUtils.serialize(message);
    }

}

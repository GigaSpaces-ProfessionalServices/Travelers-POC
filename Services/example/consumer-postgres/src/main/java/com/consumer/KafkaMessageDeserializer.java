package com.consumer;

import com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessage;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class KafkaMessageDeserializer implements Deserializer<KafkaMessage> {

    @Override
    public KafkaMessage deserialize(String topic, byte[] data) {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object obj = ois.readObject();
            if (obj instanceof KafkaMessage) {
                KafkaMessage message = (KafkaMessage) obj;
                System.out.println("Deserialized KafkaMessage: " + message); // add more structured printing
                return message;
            } else {
                throw new IllegalArgumentException("Unexpected type: " + obj.getClass());
            }
        } catch (Exception e) {
            throw new SerializationException("Error deserializing KafkaMessage", e);
        }
    }
}

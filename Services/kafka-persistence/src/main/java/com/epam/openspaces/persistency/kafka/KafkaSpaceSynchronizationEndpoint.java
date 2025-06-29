package com.epam.openspaces.persistency.kafka;

import com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessage;
import com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessageFactory;
import com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessageKey;
import com.gigaspaces.sync.IntroduceTypeData;

import kafka.javaapi.producer.Producer;

/**
 * Default implementation of Space Synchronization Endpoint which uses Apache Kafka as external data store.
 * Space synchronization operations are converted to XAP-Kafka protocol and sent to Kafka server.
 *
 * @author Oleksiy_Dyagilev
 */
public class KafkaSpaceSynchronizationEndpoint extends AbstractKafkaSpaceSynchronizationEndpoint<KafkaMessageKey, KafkaMessage> {

    private Config config;

    public KafkaSpaceSynchronizationEndpoint(Producer<KafkaMessageKey, KafkaMessage> kafkaProducer, Config config) {
        this.kafkaMessageFactory = new KafkaMessageFactory();
        this.kafkaProducer = kafkaProducer;
        this.config = config;
    }

    protected String resolveTopicForMessage(KafkaMessage message) {
        return "dih-write-back"; // TODO: read the name from config
    }

    public static class Config {
    }

    @Override
    protected boolean applyFilter(KafkaMessage message) {
        if (message.hasDataAsMap()) {
            Object zzMetaDiTimestamp = message.getDataAsMap().get("zz_META_DI_TIMESTAMP");
            return zzMetaDiTimestamp != null;
        } else {
            return true;
        }
    }

    @Override
    public void onIntroduceType(IntroduceTypeData introduceTypeData) {
        super.onIntroduceType(introduceTypeData);
        logger.info("--- Introducing type: " + introduceTypeData.getTypeDescriptor().getTypeName());
    }

}

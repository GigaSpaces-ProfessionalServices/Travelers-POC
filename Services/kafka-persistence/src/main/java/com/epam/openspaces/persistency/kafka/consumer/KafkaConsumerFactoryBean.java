package com.epam.openspaces.persistency.kafka.consumer;

import java.util.Properties;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

/**
 * Creates {@link KafkaConsumer} bean.
 */
public class KafkaConsumerFactoryBean implements FactoryBean<KafkaConsumer>, DisposableBean {

    private static final Log logger = LogFactory.getLog(KafkaConsumerFactoryBean.class);

    private Properties consumerProperties;
    private ConsumerConnector consumerConnector;

    @Override
    public KafkaConsumer getObject() throws Exception {
        logger.info("Initializing Kafka consumer");

        ConsumerConfig config = new ConsumerConfig(consumerProperties);
        consumerConnector = Consumer.createJavaConsumerConnector(config);

        return new KafkaConsumer(consumerConnector);
    }

    @Override
    public Class<?> getObjectType() {
        return KafkaConsumer.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        if (consumerConnector != null) {
            consumerConnector.shutdown();
        }
    }

    public void setConsumerProperties(Properties consumerProperties) {
        this.consumerProperties = consumerProperties;
    }

}

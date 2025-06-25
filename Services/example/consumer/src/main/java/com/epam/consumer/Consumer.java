package com.epam.consumer;

import com.epam.openspaces.persistency.kafka.consumer.KafkaConsumer;
import com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessage;
import com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessageKey;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

/**
 * Illustrates how to subscribe to Kafka topic with a help of
 * {@link com.epam.openspaces.persistency.kafka.consumer.KafkaConsumer} Consumed data is printed to log.
 */
public class Consumer implements InitializingBean, DisposableBean {

    private Logger log = Logger.getLogger(this.getClass().getName());

    private KafkaConsumer consumer;
    private ScheduledExecutorService executorService;

    public void setConsumer(KafkaConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        executorService = Executors.newScheduledThreadPool(2);

        // create two threads that consumes messages from two topics
        // one thread per topic
        final String productTopic = "product";
        final String personTopic = "person";

        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(productTopic, 1);
        topicCountMap.put(personTopic, 1);

        Map<String, List<KafkaStream<KafkaMessageKey, KafkaMessage>>> iterators = consumer.createStreams(topicCountMap);

        executorService.execute(new ConsumerTask(productTopic, iterators.get(productTopic).get(0).iterator()));
        executorService.execute(new ConsumerTask(personTopic, iterators.get(personTopic).get(0).iterator()));
    }

    private void consume(String topicName, ConsumerIterator<KafkaMessageKey, KafkaMessage> iterator) {
        log.info("Starting Kafka consumer for topic " + topicName);

        while (iterator.hasNext()) {
            KafkaMessage kafkaMessage = iterator.next().message();
            log.info("Consuming Kafka message " + kafkaMessage);
        }

    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdown();
    }

    public class ConsumerTask implements Runnable {

        private final String topicName;
        private final ConsumerIterator<KafkaMessageKey, KafkaMessage> iterator;

        @Override
        public void run() {
            consume(topicName, iterator);
        }

        public ConsumerTask(String topicName, ConsumerIterator<KafkaMessageKey, KafkaMessage> iterator) {
            this.topicName = topicName;
            this.iterator = iterator;
        }
    }

}

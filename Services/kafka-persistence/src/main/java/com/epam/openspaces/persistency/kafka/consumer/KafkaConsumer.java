package com.epam.openspaces.persistency.kafka.consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessage;
import com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessageKey;
import com.epam.openspaces.persistency.kafka.protocol.impl.serializer.KafkaMessageDecoder;
import com.epam.openspaces.persistency.kafka.protocol.impl.serializer.KafkaMessageKeyDecoder;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * Wrapper around @{link ConsumerConnector} to help configure iterators with default XAP-Kafka message protocol.
 */
public class KafkaConsumer {

    private ConsumerConnector consumerConnector;

    public KafkaConsumer(ConsumerConnector consumerConnector) {
        this.consumerConnector = consumerConnector;
    }

    /**
     * Creates iterator for given topic
     *
     * @param topic topic name
     * @return consumer iterator
     */
    public ConsumerIterator<KafkaMessageKey, KafkaMessage> createIterator(String topic) {
        return createIterators(1, topic).get(0);
    }

    /**
     * Creates iterators for given topic
     *
     * @param count number of iterators
     * @param topic topic name
     * @return list of iterators
     */
    public List<ConsumerIterator<KafkaMessageKey, KafkaMessage>> createIterators(int count, String topic) {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, count);

        Map<String, List<KafkaStream<KafkaMessageKey, KafkaMessage>>> streams = createStreams(topicCountMap);

        List<KafkaStream<KafkaMessageKey, KafkaMessage>> kafkaStreams = streams.get(topic);

        List<ConsumerIterator<KafkaMessageKey, KafkaMessage>> iterators = new ArrayList<ConsumerIterator<KafkaMessageKey, KafkaMessage>>(count);

        for (KafkaStream<KafkaMessageKey, KafkaMessage> kafkaStream : kafkaStreams) {
            iterators.add(kafkaStream.iterator());
        }

        return iterators;
    }

    /**
     * Creates streams used to consume messages
     *
     * @param topicCountMap map of [topic name, count of iterators]
     * @return map of streams, [topic name, list of streams]
     */
    public Map<String, List<KafkaStream<KafkaMessageKey, KafkaMessage>>> createStreams(Map<String, Integer> topicCountMap) {
        return consumerConnector.createMessageStreams(
                topicCountMap,
                getMessageKeyDecoder(),
                getMessageDecoder());
    }

    /**
     * @return message decoder for XAP-Kafka protocol
     */
    public KafkaMessageDecoder getMessageDecoder() {
        return new KafkaMessageDecoder();
    }

    /**
     * @return message key decoder for XAP-Kafka protocol
     */
    public KafkaMessageKeyDecoder getMessageKeyDecoder() {
        return new KafkaMessageKeyDecoder();
    }

    /**
     * Commit the offsets of all broker partitions connected by this connector.
     */
    public void commitOffsets() {
        consumerConnector.commitOffsets();
    }

    public ConsumerConnector getConsumerConnector() {
        return consumerConnector;
    }

}

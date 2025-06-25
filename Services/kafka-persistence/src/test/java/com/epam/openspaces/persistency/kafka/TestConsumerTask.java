package com.epam.openspaces.persistency.kafka;

import com.epam.openspaces.persistency.kafka.consumer.KafkaConsumer;
import com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessage;
import com.epam.openspaces.persistency.kafka.protocol.impl.KafkaMessageKey;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

public class TestConsumerTask implements Callable<List<KafkaMessage>> {
    private String topic;
    private KafkaConsumer consumer;
    private int objectCount;

    public TestConsumerTask(String topic, int objectCount, int zookeeperPort) {
        this.objectCount = objectCount;

        consumer = new KafkaConsumer(Consumer.createJavaConsumerConnector(createConsumerConfig(zookeeperPort)));
        this.topic = topic;
    }

    private ConsumerConfig createConsumerConfig(int zookeeperPort) {
        Properties props = new Properties();
        props.put("zookeeper.connect", "localhost:" + zookeeperPort);
        props.put("group.id", "0");
        props.put("zookeeper.session.timeout.ms", "1000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "smallest");

        return new ConsumerConfig(props);

    }

    @Override
    public List<KafkaMessage> call() throws Exception {
        List<KafkaMessage> result = new ArrayList<KafkaMessage>(objectCount);
        ConsumerIterator<KafkaMessageKey, KafkaMessage> iterator = consumer.createIterator(topic);
        while (iterator.hasNext()) {
            result.add(iterator.next().message());
            if (result.size() >= objectCount) {
                break;
            }
        }

        consumer.getConsumerConnector().shutdown();
        return result;
    }

}

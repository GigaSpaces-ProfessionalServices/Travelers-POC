package com.epam.openspaces.persistency.kafka;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * @author Mykola_Zalyayev
 */
public class ProducerProperties extends Properties {

    private static ProducerProperties instance;

    private int zookeeperPort;
    private int kafkaPort;

    private ProducerProperties() {
        choosePorts();
        put("metadata.broker.list", "localhost:" + kafkaPort);
        put("request.required.acks", "1");
    }

    public static ProducerProperties getInstance() {
        if (instance == null) {
            instance = new ProducerProperties();
        }
        return instance;
    }

    public int getZookeeperPort() {
        return zookeeperPort;
    }

    public int getKafkaPort() {
        return kafkaPort;
    }

    private void choosePorts() {
        List<Integer> ports;
        try {
            ports = TestUtils.choosePorts(2);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        zookeeperPort = ports.get(0);
        kafkaPort = ports.get(1);
    }
}

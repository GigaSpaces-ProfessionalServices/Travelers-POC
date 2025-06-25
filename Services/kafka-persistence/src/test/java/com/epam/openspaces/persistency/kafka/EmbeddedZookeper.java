package com.epam.openspaces.persistency.kafka;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.commons.io.FileUtils;
import org.apache.zookeeper.server.NIOServerCnxn;
import org.apache.zookeeper.server.ZooKeeperServer;

/**
 * @author Oleksiy_Dyagilev
 */
public class EmbeddedZookeper {

    private final int port;

    private NIOServerCnxn.Factory factory;
    private ZooKeeperServer zooKeeper;

    private File snapDir;
    private File logDir;

    public EmbeddedZookeper(int port) {
        this.port = port;
    }

    public void startup() throws IOException, InterruptedException {
        snapDir = TestUtils.tempDir("zookeeperSnapDir");
        logDir = TestUtils.tempDir("zookeeperLogDir");
        int tickTime = 2000;

        try {
            FileUtils.deleteDirectory(logDir);
            FileUtils.deleteDirectory(snapDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete temp dirs", e);
        }

        zooKeeper = new ZooKeeperServer(snapDir, logDir, tickTime);
        factory = new NIOServerCnxn.Factory(new InetSocketAddress(port), 0);
        factory.startup(zooKeeper);
    }

    public void shutdown() {
        zooKeeper.shutdown();
        factory.shutdown();
    }
}

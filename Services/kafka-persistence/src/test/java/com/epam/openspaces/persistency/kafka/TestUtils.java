package com.epam.openspaces.persistency.kafka;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleksiy_Dyagilev
 */
public class TestUtils {

    public static File tempDir(String name) {
        String ioDir = System.getProperty("java.io.tmpdir");
        File f = new File(ioDir, "kafka-"+name);
        return f;
    }

    /**
     * Choose a number of random available ports
     */
    public static List<Integer> choosePorts(int count) throws IOException {
        List<Integer> ports = new ArrayList<Integer>(count);
        List<ServerSocket> sockets = new ArrayList<ServerSocket>(count);
        for (int i = 0; i < count; i++) {
            try {
                ServerSocket socket = new ServerSocket(0);

                sockets.add(socket);
            } catch (IOException e) {
                throw new IOException("No available socket");
            }
        }
        for (ServerSocket socket: sockets){
            ports.add(socket.getLocalPort());
            socket.close();
        }
        return ports;
    }

}

package com.nhnacademy.xflow2.node;

import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ModbusInTest {
    private final int DEFAULT_PORT = 1512;
    @Test
    public synchronized void connectionTest() throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);
        ModbusIn modbusIn = new ModbusIn(1, serverSocket);

        modbusIn.start();

        Socket socket = new Socket("localhost", DEFAULT_PORT);

        byte[] bytes = {0, 0, 0, 0, 0};
        socket.getOutputStream().write(bytes);
        socket.getOutputStream().flush();

        wait(1000);
    }
}

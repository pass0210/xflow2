package com.nhnacademy.xflow2.node;

import com.nhnacademy.xflow2.message.ByteWithSocketMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

@Slf4j
public class ModbusIn extends OutputNode<ByteWithSocketMessage> {
    private final ServerSocket serverSocket;
    public ModbusIn(int outputCount, ServerSocket serverSocket) {
        super(outputCount);
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();

                new Thread(() -> {
                    try {
                        BufferedInputStream reader = new BufferedInputStream(socket.getInputStream());

                        byte[] buffer = new byte[1024];
                        int receivedLength = reader.read(buffer);

                        if (receivedLength != 0) {
                            byte[] receivedBytes = Arrays.copyOfRange(buffer, 0, receivedLength);
                            ByteWithSocketMessage message = new ByteWithSocketMessage(socket, receivedBytes);

                            log.info("{}", Arrays.toString(message.getPayload()));

                            output(0, message);
                        }
                    } catch (IOException e) {
                        log.error("socket reader error: {}", e.getMessage());
                    } catch (InterruptedException e) {
                        log.error("port error: {}", e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                }).start();
            } catch (IOException e) {
                log.error("server socket error: {}", e.getMessage());
            }
        }
    }
}

package com.nhnacademy.xflow2.node;

import com.nhnacademy.xflow2.message.ByteWithSocketMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Modbus 프로토콜을 사용하여 데이터를 입력받는 서버 클래스입니다.
 * Modbus TCP 서버로 동작하며, 클라이언트로부터 데이터를 수신합니다.
 */
@Slf4j
public class ModbusIn extends OutputNode<ByteWithSocketMessage> {
    private static final int MINIMUM_PACKET_LENGTH = 12;

    private final ServerSocket serverSocket;

    public ModbusIn(int outputCount, ServerSocket serverSocket) {
        super(outputCount);
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();

                new Thread(() -> {
                    try {
                        BufferedInputStream reader = new BufferedInputStream(socket.getInputStream());

                        byte[] buffer = new byte[1024];
                        int receivedLength = reader.read(buffer);

                        if (receivedLength >= MINIMUM_PACKET_LENGTH) {
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

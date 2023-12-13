package com.nhnacademy.xflow2.client;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ModbusClientManager {
    private static Map<String, Socket> socketMap = new HashMap<>();

    private ModbusClientManager() {
        throw new IllegalStateException();
    }

    public static Socket getSocket(String serverAddress, int port) throws IOException {
        String key = generateKey(serverAddress, port);

        if (socketMap.containsKey(key)) { // 소켓이 존재 할 경우 기존 소켓 반환
            return socketMap.get(key);

        } else {
            try {
                Socket newSocket = new Socket(serverAddress, port);
                socketMap.put(key, newSocket);
                return newSocket;

            } catch (IOException e) {
                // 소켓 생성 중 예외 발생 시 RuntimeException으로 감싸서 전파
                throw new RuntimeException("소켓을 생성하지 못하였습니다.", e);
            }
        }
    }

    // 서버 주소와 포트를 구분자로 사용하여 키 생성
    private static String generateKey(String serverAddress, int port) {
        return serverAddress + "-" + port;
    }
}

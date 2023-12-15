package com.nhnacademy.xflow2.message;

import java.net.Socket;

/**
 * 소켓과 관련된 메시지를 나타내는 추상 클래스입니다.
 * {@code Socket} 객체를 갖고 있으며, 하위 클래스에서 구체적인 동작을 정의할 수 있습니다.
 */
public abstract class SocketMessage implements Message {
    private final Socket socket;

    protected SocketMessage(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }
}

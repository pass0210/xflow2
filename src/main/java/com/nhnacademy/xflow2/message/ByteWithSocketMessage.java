package com.nhnacademy.xflow2.message;

import java.net.Socket;

/**
 * 소켓과 연결된 바이트 데이터를 나타내는 메시지 클래스입니다.
 * 소켓과 연결된 데이터를 관리하며, 페이로드로 바이트 배열을 제공합니다.
 */
public class ByteWithSocketMessage extends SocketMessage {
    private byte[] bytes;

    public ByteWithSocketMessage(Socket socket, byte[] bytes) {
        super(socket);
        this.bytes = bytes;
    }

    @Override
    public byte[] getPayload() {
        return bytes;
    }
}

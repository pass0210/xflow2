package com.nhnacademy.xflow2.message;

import java.net.Socket;

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

package com.nhnacademy.xflow2.message;

import org.json.JSONObject;

import java.net.Socket;

public class JSONWithSocketMessage extends SocketMessage {
    JSONObject jsonObject;

    public JSONWithSocketMessage(Socket socket, JSONObject jsonObject) {
        super(socket);
        this.jsonObject = jsonObject;
    }

    @Override
    public JSONObject getPayload() {
        return jsonObject;
    }
}

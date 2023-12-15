package com.nhnacademy.xflow2.message;

import org.json.JSONObject;

import java.net.Socket;

/**
 * JSON 데이터와 소켓과 관련된 메시지를 나타내는 클래스입니다.
 * 소켓 통신에 필요한 정보를 추가로 포함하고 있습니다.
 */
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

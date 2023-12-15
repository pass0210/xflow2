package com.nhnacademy.xflow2.message;

import org.json.JSONObject;

/**
 * JSON 형식의 메시지를 나타내는 클래스입니다.
 * 메시지의 데이터와 송수신과 관련된 정보를 포함하고 있습니다.
 */
public class JSONMessage implements Message {
    JSONObject jsonObject;

    public JSONMessage(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public JSONObject getPayload() {
        return jsonObject;
    }
}

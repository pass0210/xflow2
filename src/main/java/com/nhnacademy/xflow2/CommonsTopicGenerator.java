package com.nhnacademy.xflow2;

import org.json.JSONObject;

/**
 * 주어진 JSONObject를 기반으로 토픽을 생성하는 클래스입니다.
 * "branch", "place", "device", "name", "sensorType" 등의 키를 활용하여 토픽을 구성하며,
 * StringBuilder를 사용하여 효율적으로 문자열을 생성합니다.
 */
public class CommonsTopicGenerator {

    public StringBuilder generate(JSONObject object) {
        StringBuilder builder = new StringBuilder();
        builder.append("data")
                .append("/b/" + object.get("branch"))
                .append("/p/" + object.get("place"))
                .append("/d/" + object.get("device"))
                .append("/n/" + object.get("name"))
                .append("/e/" + object.get("sensorType"));
        return builder;
    }
}
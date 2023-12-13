package com.nhnacademy.xflow2;

import org.json.JSONObject;

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
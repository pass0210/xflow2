package com.nhnacademy.xflow2.node;

import org.json.JSONObject;

import com.nhnacademy.xflow2.CommonsTopicGenerator;
import com.nhnacademy.xflow2.message.JSONMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttMessageGenerator extends InputOutputNode<JSONMessage, JSONMessage> {
    CommonsTopicGenerator topicGenerator;

    public MqttMessageGenerator(int inputCount, int outputCount) {
        super(inputCount, outputCount);
        topicGenerator = new CommonsTopicGenerator();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                JSONMessage message = tryGetMessage();
                JSONObject object = message.getPayload();

                JSONObject o = new JSONObject();
                o.put("topic", topicGenerator.generate(object).toString());
                o.put("time", System.currentTimeMillis() / 1000L);
                o.put("value", (object.optDouble("value")));

                log.info("messageGenerator");

                output(0, new JSONMessage(o));
            } catch (InterruptedException e) {
                log.error("interrupt error: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

}

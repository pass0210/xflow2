package com.nhnacademy.xflow2.node;

import com.nhnacademy.xflow2.message.JSONMessage;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

@Slf4j
public class MqttSubscriber extends OutputNode<JSONMessage> {
    private String topicFilter;
    private final MqttClient client;

    public MqttSubscriber(int outputCount, MqttClient client, String topicFilter) {
        super(outputCount);
        this.client = client;
        this.topicFilter = topicFilter;
    }

    @Override
    public void run() {
        try {
            // subscribe 생성
            client.subscribe(topicFilter, (topic, msg) -> {
                // JSONObject 생성
                JSONObject object = new JSONObject(new String(msg.getPayload()));
                JSONMessage message = new JSONMessage(object);

                log.info("{}", object);

                // Output
                output(0, message);
            });
        } catch (MqttException e) {
            log.error("Mqtt Error: {}", e.getMessage());
        }
    }
}
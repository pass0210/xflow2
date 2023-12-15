package com.nhnacademy.xflow2.node;

import com.nhnacademy.xflow2.message.JSONMessage;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

/**
 * MQTT 프로토콜을 사용하여 데이터를 구독하는 클래스입니다.
 * 지정된 MQTT 브로커에서 데이터를 구독하고 수신된 데이터를 처리합니다.
 */
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

                // Output
                output(0, message);
            });
        } catch (MqttException e) {
            log.error("Mqtt Error: {}", e.getMessage());
        }
    }
}
package com.nhnacademy.xflow2.node;

import com.nhnacademy.xflow2.message.JSONMessage;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

/**
 * MQTT 프로토콜을 사용하여 데이터를 발행하는 클래스입니다.
 * 지정된 MQTT 브로커에 데이터를 발행합니다.
 */
@Slf4j
public class MqttPublisher extends InputNode<JSONMessage> {
    private final MqttClient client;

    public MqttPublisher(int inputCount, MqttClient client) {
        super(inputCount);
        this.client = client;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                JSONMessage message = tryGetMessage();
                JSONObject jsonObject = message.getPayload();
                MqttMessage mqttMessage = new MqttMessage(jsonObject.toString().getBytes());
                client.publish(jsonObject.getString("topic"), mqttMessage);
            } catch (MqttException e) {
                log.error("Mqtt Error : {}", e.getMessage());
            } catch (InterruptedException e) {
                log.error("Thread Error : {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }
}

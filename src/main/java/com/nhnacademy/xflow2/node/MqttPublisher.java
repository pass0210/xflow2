package com.nhnacademy.xflow2.node;

import com.nhnacademy.xflow2.message.JSONMessage;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

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
                log.info("{}", jsonObject);
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

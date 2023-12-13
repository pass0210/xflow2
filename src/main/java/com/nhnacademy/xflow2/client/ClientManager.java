package com.nhnacademy.xflow2.client;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientManager {
    MqttClient subClient;
    MqttClient pubClient;

    public ClientManager(String subURI, String pubURI) {
        try {
            subClient = new MqttClient(subURI, UUID.randomUUID().toString());
            pubClient = new MqttClient(pubURI, UUID.randomUUID().toString());
        } catch (MqttException e) {
            log.error("mqtt error: {}", e.getMessage());
        }
    }

    public void mqttConnect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);

        try {
            subClient.connect(options);
            pubClient.connect(options);
        } catch (MqttException e) {
            log.error("mqtt error: {}", e.getMessage());
        }
    }

    public MqttClient getSubClient() {
        return subClient;
    }

    public MqttClient getPubClient() {
        return pubClient;
    }
}

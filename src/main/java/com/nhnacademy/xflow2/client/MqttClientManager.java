package com.nhnacademy.xflow2.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import lombok.extern.slf4j.Slf4j;

/**
 * MQTT 클라이언트를 생성하고 관리하는 유틸리티 클래스입니다.
 * 서버 URI를 기반으로 중복 생성을 방지하며, 연결 옵션을 설정하여 안정적인 연결을 유지합니다.
 */
@Slf4j
public class MqttClientManager {
    private static Map<String, MqttClient> map = new HashMap<>();

    private MqttClientManager() {
        throw new IllegalStateException();
    }

    public static MqttClient getClient(String serverURI) {
        if (map.containsKey(serverURI)) {
            return map.get(serverURI);
        } else {
            MqttClient client = null;
            try {
                client = new MqttClient(serverURI, UUID.randomUUID().toString());
                setOption(client);
                map.put(serverURI, client);
            } catch (MqttException e) {
                log.error("mqtt error: {}", e.getMessage());
            }
            return client;
        }
    }

    private static void setOption(MqttClient client) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        try {
            client.connect(options);
        } catch (MqttSecurityException e) {
            log.error("mqtt security error: {}", e.getMessage());
        } catch (MqttException e) {
            log.error("mqtt error: {}", e.getMessage());
        }
    }
}

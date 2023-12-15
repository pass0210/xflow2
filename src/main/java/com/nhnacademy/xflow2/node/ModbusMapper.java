package com.nhnacademy.xflow2.node;

import java.net.Socket;

import org.json.JSONObject;

import com.nhnacademy.xflow2.message.JSONMessage;
import com.nhnacademy.xflow2.message.JSONWithSocketMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * Modbus 데이터와 JSON 데이터 간의 매핑을 수행하는 클래스입니다.
 * Modbus 프로토콜로부터 수신된 데이터를 JSON 형식으로 변환하거나 그 반대로 수행합니다.
 */
@Slf4j
public class ModbusMapper extends InputOutputNode<JSONWithSocketMessage, JSONMessage> {
    JSONObject mappingTable;

    public ModbusMapper(int inputCount, int outputCount, JSONObject mappingTable) {
        super(inputCount, outputCount);
        this.mappingTable = mappingTable;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                JSONWithSocketMessage receiveMessage = tryGetMessage();
                JSONObject receiveJson = receiveMessage.getPayload();
                Socket socket = receiveMessage.getSocket();
                String host = socket.getInetAddress().getHostName();
                int port = socket.getPort();
                String mappingKey = host + "-" + port;

                String address = receiveJson.optString("address");
                double value = receiveJson.optDouble("value");

                JSONObject mappingData = mappingTable.getJSONObject(mappingKey).getJSONObject(address);
                JSONObject sendJsonObject = new JSONObject(mappingData.toString());

                sendJsonObject.put("value", value);
                log.debug("{}", sendJsonObject);
                JSONMessage sendMessage = new JSONMessage(sendJsonObject);

                output(0, sendMessage);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

    }

}

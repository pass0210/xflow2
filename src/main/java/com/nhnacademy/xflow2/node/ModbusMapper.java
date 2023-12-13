package com.nhnacademy.xflow2.node;

import java.net.Socket;

import org.json.JSONObject;

import com.nhnacademy.xflow2.message.JSONMessage;
import com.nhnacademy.xflow2.message.JSONWithSocketMessage;

public class ModbusMapper extends InputOutputNode<JSONWithSocketMessage, JSONMessage> {
    JSONObject mappingTable;

    public ModbusMapper(int inputCount, int outputCount, JSONObject mappingTable) {
        super(inputCount, outputCount);
        this.mappingTable = mappingTable;
    }

    @Override
    public void run() {
        try {
            JSONWithSocketMessage receiveMessage = tryGetMessage();
            JSONObject receiveJson = receiveMessage.getPayload();
            Socket socket = receiveMessage.getSocket();
            String host = socket.getInetAddress().getHostName();
            int port = socket.getPort();
            String mappingKey = host + port;

            String address = receiveJson.getString("address");
            double value = receiveJson.getDouble("value");

            JSONObject mappingData = mappingTable.getJSONObject(mappingKey).getJSONObject(address);
            JSONObject sendJsonObject = new JSONObject(mappingData.toString());

            sendJsonObject.put("value", value);
            JSONMessage sendMessage = new JSONMessage(sendJsonObject);

            output(0, sendMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

    }

}

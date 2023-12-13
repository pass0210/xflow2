package com.nhnacademy.xflow2.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import org.json.JSONObject;

import com.nhnacademy.xflow2.message.JSONWithSocketMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModbusClient extends OutputNode<JSONWithSocketMessage>{
    static final int TIME = 1000;
    static final byte UNIT_ID = 1;
    static final byte FUNCTION_CODE = 4;
    static final byte ADDRESS = 101;
    static final byte VALUE = 1;

    Socket client;
    BufferedInputStream inputStream;
    BufferedOutputStream outputStream;

    protected ModbusClient(int outputCount, Socket client) {
        super(outputCount);
        try {
            inputStream = new BufferedInputStream(client.getInputStream());
            outputStream = new BufferedOutputStream(client.getOutputStream());
            this.client = client;
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                sendRequest();
                responseProcessing();
                Thread.sleep(TIME);
            } catch (IOException | InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void sendRequest() throws IOException {
        byte[] requestMessage = {0, 1, 0, 0, 0, 6, UNIT_ID, FUNCTION_CODE, 0, ADDRESS, 0, VALUE};
        outputStream.write(requestMessage);
        outputStream.flush();
    }

    private void responseProcessing() throws IOException, InterruptedException {
        byte[] inputBuffer = new byte[1024];
        int receivedLength = inputStream.read(inputBuffer, 0, inputBuffer.length);
        if (receivedLength > 0) {
            byte[] recievedMessage = Arrays.copyOfRange(inputBuffer, 0, receivedLength);
            output(0, new JSONWithSocketMessage(client, byteToJson(recievedMessage)));
        }
    }
    
    private JSONObject byteToJson(byte[] msg) {
        int adress = msg[8] << 8 | (msg[9] & 0xff);
        int value = msg[10] << 8 | (msg[11] & 0xff);

        JSONObject o = new JSONObject();
        o.put("address", adress);
        o.put("value", value);
        return o;
    }
}

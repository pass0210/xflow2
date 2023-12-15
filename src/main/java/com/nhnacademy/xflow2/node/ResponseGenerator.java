package com.nhnacademy.xflow2.node;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.nhnacademy.xflow2.message.ByteWithSocketMessage;
import com.nhnacademy.xflow2.message.JSONWithSocketMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseGenerator extends InputOutputNode<JSONWithSocketMessage, ByteWithSocketMessage> {

    public ResponseGenerator(int inputCount, int outputCount) {
        super(inputCount, outputCount);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                JSONWithSocketMessage jsonWithSocketMessage = tryGetMessage();
                JSONObject info = jsonWithSocketMessage.getPayload();
                Socket socket = jsonWithSocketMessage.getSocket();

                List<Byte> adu = putMBAP(info, putPDU(info));

                byte[] byteArray = new byte[adu.size()];

                for (int i = 0; i < byteArray.length; i++) {
                    byteArray[i] = adu.get(i);
                }

                ByteWithSocketMessage byteWithSocketMessage = new ByteWithSocketMessage(socket, byteArray);

                output(0, byteWithSocketMessage);
            } catch (InterruptedException e) {
                log.error("response error: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    private List<Byte> putMBAP(JSONObject info, List<Byte> pdu) {
        List<Byte> adu = new ArrayList<>();

        int transactionId = info.optInt("transactionId");
        adu.add((byte) (transactionId >> 8 & 0xff));
        adu.add((byte) (transactionId & 0xff));

        int protocolId = info.optInt("protocolId");
        adu.add((byte) (protocolId >> 8 & 0xff));
        adu.add((byte) (protocolId & 0xff));

        int length = pdu.size() + 1;
        adu.add((byte) ((length >> 8) & 0xFF));
        adu.add((byte) (length & 0xFF));

        int unitId = info.optInt("unitId");
        adu.add((byte) (unitId & 0xff));

        adu.addAll(pdu);

        return adu;
    }

    private List<Byte> putPDU(JSONObject info) {
        List<Byte> pdu = new ArrayList<>();
        // 분기 처리
        int functionCode = info.optInt("functionCode");

        switch (functionCode) {
            case 3:
            case 4:
                pdu.add((byte) (functionCode & 0xFF));

                int dataByteLength = info.optInt("dataByteLength");
                pdu.add((byte) (dataByteLength & 0xff));
                break;
            case 6:
            case 16:
                pdu.add((byte) (functionCode & 0xFF));

                int startAddress = info.optInt("startAddress");
                pdu.add((byte) ((startAddress >> 8) & 0xFF));
                pdu.add((byte) (startAddress & 0xFF));
                break;
            default:
                break;
        }

        int[] dataArray = (int[]) info.get("data");
        for (int data : dataArray) {
            pdu.add((byte) ((data >> 8) & 0xff));
            pdu.add((byte) (data & 0xff));
        }

        return pdu;
    }
}
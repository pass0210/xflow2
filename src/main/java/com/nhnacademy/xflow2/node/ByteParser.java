package com.nhnacademy.xflow2.node;

import lombok.extern.slf4j.Slf4j;
import java.net.Socket;
import org.json.JSONObject;
import com.nhnacademy.xflow2.message.ByteWithSocketMessage;
import com.nhnacademy.xflow2.message.JSONWithSocketMessage;

@Slf4j
public class ByteParser extends InputOutputNode<ByteWithSocketMessage, JSONWithSocketMessage> {

    public ByteParser(int inputCount, int outputCount) {
        super(inputCount, outputCount);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ByteWithSocketMessage message = tryGetMessage();
                Socket socket = message.getSocket();
                byte[] data = message.getPayload();
                JSONObject jsonObject = parseData(data);
                JSONWithSocketMessage jsonWithSocketMessage = new JSONWithSocketMessage(socket, jsonObject);

                output(0, jsonWithSocketMessage); // 변환된 데이터를 Output으로 전달
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IndexOutOfBoundsException e) {
                log.error("index error: {}", e.getMessage());
            }
        }
    }

    private JSONObject parseData(byte[] byteData) throws IndexOutOfBoundsException {
        JSONObject result = new JSONObject();

        int transactionId = (byteData[0] & 0xFF) << 8 | (byteData[1] & 0xFF);
        result.put("transactionId", transactionId);

        int protocolId = (byteData[2] & 0xFF) << 8 | (byteData[3] & 0xFF);
        result.put("protocolId", protocolId);

        int length = (byteData[4] & 0xFF) << 8 | (byteData[5] & 0xFF);
        result.put("length", length);

        int unitId = byteData[6] & 0xFF;
        result.put("unitId", unitId);

        int functionCode = byteData[7] & 0xFF;
        result.put("functionCode", functionCode);

        int startAddress = (byteData[8] & 0xFF) << 8 | (byteData[9] & 0xFF);
        result.put("startAddress", startAddress);

        if (functionCode == 16) {
            int dataCount = (byteData[10] & 0xFF) << 8 | (byteData[11] & 0xFF);
            result.put("dataCount", dataCount);

            int dataByteLength = byteData[12] & 0xFF;
            result.put("dataByteLength", dataByteLength);

            int[] data = new int[dataCount];

            for (int i = 0; i < dataCount; i++) {
                data[i] = (byteData[13 + i * 2] << 8) | (byteData[14 + i * 2] & 0xFF);
            }
            result.put("data", data);
        } else {
            int[] data = new int[1];
            data[0] = (byteData[10] << 8) | (byteData[11] & 0xFF);
            result.put("data", data);
        }
        int totalLength = byteData.length;
        result.put("totalLength", totalLength);

        return result;
    }
}

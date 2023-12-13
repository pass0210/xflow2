package com.nhnacademy.xflow2.node;

import com.nhnacademy.xflow2.message.JSONMessage;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

@Slf4j
public class RuleEngine extends InputOutputNode<JSONMessage, JSONMessage> {
    private final JSONObject database;

    protected RuleEngine(int inputCount, int outputCount, JSONObject database) {
        super(inputCount, outputCount);
        this.database = database;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                JSONMessage receivedMessage = tryGetMessage();

                JSONObject payload = receivedMessage.getPayload();

                String key = payload.optString("key");
                String value = payload.optString("value");

                JSONObject sendObject = new JSONObject(database.getJSONObject(key));
                sendObject.put("value", value);

                JSONMessage sendMessage = new JSONMessage(sendObject);

                output(0, sendMessage);
                // TODO : output 1 추가
            } catch (InterruptedException e) {
                log.error("message 가져오기 실패: {}", e.getMessage());
            }
        }
    }
}

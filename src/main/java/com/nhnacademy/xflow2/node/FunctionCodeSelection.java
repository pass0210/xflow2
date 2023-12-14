package com.nhnacademy.xflow2.node;

import org.json.JSONObject;

import com.nhnacademy.xflow2.message.JSONWithSocketMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FunctionCodeSelection extends InputOutputNode<JSONWithSocketMessage, JSONWithSocketMessage> {

    public FunctionCodeSelection(int inputCount, int outputCount) {
        super(inputCount, outputCount);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                JSONWithSocketMessage message = tryGetMessage();
                JSONObject jsonObject = message.getPayload();
                int functionCode = jsonObject.optInt("functionCode");

                switch (functionCode) {
                    case 3:
                    case 4:
                        output(1, message);
                        break;
                    case 6:
                    case 16:
                        output(0, message);
                        break;
                    default:
                }
            } catch (InterruptedException e) {
                log.error("select error: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

}

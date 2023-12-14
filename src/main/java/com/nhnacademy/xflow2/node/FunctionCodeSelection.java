package com.nhnacademy.xflow2.node;

import org.json.JSONObject;

import com.nhnacademy.xflow2.message.JSONWithSocketMessage;

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

                if (functionCode == 6 || functionCode == 16) {
                    output(0, message);
                } else if (functionCode == 3 || functionCode == 4) {
                    output(1, message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

}

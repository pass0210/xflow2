package com.nhnacademy.xflow2.node;

import org.json.JSONObject;

import com.nhnacademy.xflow2.message.JSONWithSocketMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * Modbus 프로토콜의 Function Code를 선택하는 클래스입니다.
 * 주어진 데이터에서 Function Code를 추출하거나 설정합니다.
 */
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

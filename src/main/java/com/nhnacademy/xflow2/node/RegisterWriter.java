package com.nhnacademy.xflow2.node;

import java.util.Arrays;

import org.json.JSONObject;

import com.nhnacademy.xflow2.message.JSONWithSocketMessage;
import com.nhnacademy.xflow2.register.Register;

import lombok.extern.slf4j.Slf4j;

/**
 * Modbus 레지스터에 값을 쓰는 클래스입니다.
 * 주어진 주소에 값을 쓰고 레지스터를 업데이트합니다.
 */
@Slf4j
public class RegisterWriter extends InputOutputNode<JSONWithSocketMessage, JSONWithSocketMessage> {
    private int[] register = Register.INSTANCE.getHoldingRegisters();

    public RegisterWriter(int inputCount, int outputCount) {
        super(inputCount, outputCount);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                JSONWithSocketMessage msg = tryGetMessage();
                JSONObject o = msg.getPayload();

                int startAddress = o.getInt("startAddress");
                int[] data = ((int[]) o.get("data"));

                if (o.getInt("functionCode") == 6) {
                    singleWrite(startAddress, data[0]);
                } else {
                    int dataCount = o.getInt("dataCount");
                    multiWrite(startAddress, dataCount, data);
                    int[] intArray = new int[] { dataCount };
                    o.put("data", intArray);
                }

                log.debug("registers: {}",
                        Arrays.copyOfRange(Register.INSTANCE.getHoldingRegisters(), 100, 105));

                output(0, new JSONWithSocketMessage(msg.getSocket(), o));
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    private void singleWrite(int startAddress, int data) {
        register[startAddress] = data;
    }

    private void multiWrite(int startAddress, int dataCount, int[] data) {
        for (int i = 0; i < dataCount; i++) {
            register[startAddress + i] = data[i];
        }
    }
}
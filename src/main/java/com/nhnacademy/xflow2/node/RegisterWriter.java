package com.nhnacademy.xflow2.node;

import org.json.JSONObject;

import com.nhnacademy.xflow2.message.JSONWithSocketMessage;
import com.nhnacademy.xflow2.register.Register;

import lombok.extern.slf4j.Slf4j;

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
                int dataCount = o.getInt("dataCount");
                int[] data = ((int[]) o.get("data"));

                if (o.getInt("functionCode") == 6) {
                    singleWrite(startAddress, data[0]);
                } else {
                    multiWrite(startAddress, dataCount, data);
                    o.put("data", dataCount);
                }

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
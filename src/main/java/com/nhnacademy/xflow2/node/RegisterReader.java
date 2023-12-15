package com.nhnacademy.xflow2.node;

import java.net.Socket;

import org.json.JSONObject;

import com.nhnacademy.xflow2.message.JSONWithSocketMessage;
import com.nhnacademy.xflow2.register.Register;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegisterReader extends InputOutputNode<JSONWithSocketMessage, JSONWithSocketMessage> {

    public RegisterReader(int inputCount, int outputCount) {
        super(inputCount, outputCount);
    }

    @Override
    public void run() {
        try {
            JSONWithSocketMessage message = tryGetMessage();
            Socket socket = message.getSocket();
            JSONObject jsonObject = message.getPayload();

            int functionCode = jsonObject.getInt("functionCode");
            int startAddress = jsonObject.getInt("startAddress");
            int dataCount = ((int[]) jsonObject.get("data"))[0];

            int[] registers;
            int[] readRegisters;

            if (functionCode == 3) {
                registers = Register.INSTANCE.getHoldingRegisters();
            } else {
                registers = Register.INSTANCE.getInputRegisters();
            }

            readRegisters = new int[dataCount];

            // 원본 배열, 원본 배열의 복사 시작 위치, 복사할 배열, 복사할 배열의 복사 시작 위치, 복사할 요소의 개수
            System.arraycopy(registers, startAddress, readRegisters, 0, dataCount);

            jsonObject.put("data", readRegisters);
            jsonObject.put("dataByteLength", readRegisters.length * 2);

            output(0, new JSONWithSocketMessage(socket, jsonObject));

        } catch (InterruptedException e) {
            log.error("RegisterReader Error : {} ", e.getMessage());
            Thread.currentThread().interrupt();
        }

    }

}

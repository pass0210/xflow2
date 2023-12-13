package com.nhnacademy.xflow2.node;

import org.json.JSONObject;

import com.nhnacademy.xflow2.message.JSONMessage;
import com.nhnacademy.xflow2.register.Register;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegisterUpdater extends InputNode<JSONMessage>{
    Register register;

    public RegisterUpdater(int inputCount, Register register){
        super(inputCount);
        this.register = register;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try {
                JSONMessage message = tryGetMessage();
                JSONObject object = message.getPayload();
                
                String registerInfo = object.getString("register");
                int address = object.getInt("address");
                int value = object.getInt("value");

                if(registerInfo.equals("holding")){
                    int[] holdingRegisters = register.getHoldingRegisters();
                    holdingRegisters[address] = value;
                }else if(registerInfo.equals("input")){
                    int[] holdingRegisters = register.getInputRegisters();
                    holdingRegisters[address] = value;
                }

            } catch (InterruptedException e) {
                log.error("RegisterUpdate Error : {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }
    
}

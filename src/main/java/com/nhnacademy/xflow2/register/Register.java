package com.nhnacademy.xflow2.register;

public class Register {
    private int[] holdingRegisters = new int[10000];
    private int[] inputRegisters = new int[10000];

    public Register(){
        for(int i = 0; i < holdingRegisters.length ; i++){
            holdingRegisters[i] = 0;
        }

        for(int i = 0; i < inputRegisters.length ; i++){
            inputRegisters[i] = 0;
        }
    }
    
    public int[] getHoldingRegisters() {
        return holdingRegisters;
    }
    public int[] getInputRegisters() {
        return inputRegisters;
    }

}

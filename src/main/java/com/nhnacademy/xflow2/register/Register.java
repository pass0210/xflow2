package com.nhnacademy.xflow2.register;

public enum Register {
    INSTANCE;

    private int[] holdingRegisters = new int[10000];
    private int[] inputRegisters = new int[10000];
    
    public int[] getHoldingRegisters() {
        return holdingRegisters;
    }
    public int[] getInputRegisters() {
        return inputRegisters;
    }
}

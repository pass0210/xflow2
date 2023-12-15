package com.nhnacademy.xflow2.register;

/**
 * 레지스터 데이터를 관리하는 싱글톤 열거형 클래스로, Holding Registers와 Input Registers를 제공합니다.
 * 레지스터에 대한 데이터를 읽고 쓰기 위해 사용됩니다.
 */
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

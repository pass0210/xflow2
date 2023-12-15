package com.nhnacademy.xflow2.node;

import com.nhnacademy.xflow2.port.Port;

/**
 * 입력을 처리하는 추상 노드 클래스입니다.
 * {@code Port}를 통해 입력을 받아 처리하며, 별도의 스레드에서 실행됩니다.
 */
public abstract class InputNode<T> extends Node implements Runnable {
    private final Thread thread;
    private final Port<T>[] inputPorts;

    @SuppressWarnings("unchecked")
    protected InputNode(int inputCount) {
        thread = new Thread(this);
        inputPorts = new Port[inputCount];

        for (int i = 0; i < inputCount; i++) {
            inputPorts[i] = new Port<>();
        }
    }

    public void start() {
        thread.start();
    }

    public Port<T> getInputPort(int index) {
        return inputPorts[index];
    }

    protected T tryGetMessage() throws InterruptedException {
        return inputPorts[0].get();
    }
}

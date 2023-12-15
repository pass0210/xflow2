package com.nhnacademy.xflow2.node;

import com.nhnacademy.xflow2.port.Port;

/**
 * 입력과 출력을 모두 처리하는 추상 노드 클래스입니다.
 * 별도의 스레드에서 실행되며, 입력 및 출력 포트를 관리합니다.
 */
public abstract class InputOutputNode<T, G> extends Node implements Runnable {
    private final Thread thread;
    private final Port<T>[] inputPorts;
    private final Port<G>[] outputPorts;

    @SuppressWarnings("unchecked")
    protected InputOutputNode(int inputCount, int outputCount) {
        thread = new Thread(this);
        inputPorts = new Port[inputCount];
        outputPorts = new Port[outputCount];

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

    public void connect(int index, Port<G> inputPort) {
        outputPorts[index] = inputPort;
    }

    protected void output(int index, G message) throws InterruptedException {
        outputPorts[index].put(message);
    }

    protected T tryGetMessage() throws InterruptedException {
        return inputPorts[0].get();
    }
}

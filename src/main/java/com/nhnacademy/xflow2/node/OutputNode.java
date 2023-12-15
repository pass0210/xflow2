package com.nhnacademy.xflow2.node;

import com.nhnacademy.xflow2.port.Port;

/**
 * 출력을 담당하는 노드의 추상 클래스로, 모든 출력 노드는 이 클래스를 상속받습니다.
 * 별도의 스레드에서 실행되며, 출력 포트에 메시지를 전달하는 기능을 제공합니다.
 */
public abstract class OutputNode<T> extends Node implements Runnable {
    private final Thread thread;
    private final Port<T>[] outputPorts;

    @SuppressWarnings("unchecked")
    protected OutputNode(int outputCount) {
        thread = new Thread(this);
        outputPorts = new Port[outputCount];
    }

    public void start() {
        thread.start();
    }

    public void connect(int index, Port<T> inputPort) {
        outputPorts[index] = inputPort;
    }

    protected void output(int index, T message) throws InterruptedException {
        outputPorts[index].put(message);
    }
}

package com.nhnacademy.xflow2.port;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 메시지를 주고받는 포트를 나타내는 클래스로, 블로킹 큐를 사용하여 메시지를 처리합니다.
 * 입력 노드와 출력 노드 간의 통신에 사용됩니다.
 */
public class Port<T> {
    BlockingQueue<T> messageQueue;

    public Port() {
        messageQueue = new LinkedBlockingQueue<>();
    }

    public void put(T message) throws InterruptedException {
        messageQueue.put(message);
    }

    public T get() throws InterruptedException {
        return messageQueue.take();
    }
}

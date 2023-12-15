package com.nhnacademy.xflow2.node;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 노드의 추상 클래스로, 모든 노드는 이 클래스를 상속받습니다.
 * 고유한 식별자(ID)를 생성하며, 노드의 공통적인 속성을 제공합니다.
 */
@Getter
public abstract class Node {
    private static final AtomicInteger COUNT = new AtomicInteger(0);
    private final String id;

    protected Node() {
        id = String.format("%s-%02d", getClass().getSimpleName(), COUNT.incrementAndGet());
    }
}

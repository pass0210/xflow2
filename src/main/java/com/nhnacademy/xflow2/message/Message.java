package com.nhnacademy.xflow2.message;

/**
 * 메시지의 페이로드를 제공하는 인터페이스입니다.
 * 구현체는 실제 데이터를 반환하는 {@code getPayload} 메서드를 제공해야 합니다.
 */
public interface Message {
    Object getPayload();
}

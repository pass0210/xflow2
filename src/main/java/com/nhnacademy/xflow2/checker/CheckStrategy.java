package com.nhnacademy.xflow2.checker;

/**
 * JSON 데이터의 Key에 대한 조건을 확인하는 전략 클래스입니다.
 * 특정 Key 또는 Key의 하위 키에 대한 검증을 수행합니다.
 */
public interface CheckStrategy<T> {
    boolean check(T data);
}

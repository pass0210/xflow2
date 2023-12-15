package com.nhnacademy.xflow2.checker;

import lombok.extern.slf4j.Slf4j;

/**
 * JSON 데이터의 특정 조건을 확인하는 역할을 하는 클래스입니다.
 * 다양한 전략(Strategy)를 사용하여 조건을 확인하며, 입력 데이터를 검증합니다.
 */
@Slf4j
public class Checker<T> {
   
    private final CheckStrategy<T> checkStrategy;

    public Checker(CheckStrategy<T> checkStrategy) {
        this.checkStrategy = checkStrategy;
    }

    @SuppressWarnings("unchecked")
    public boolean check(Object data) {
        try {
            return checkStrategy.check((T) data);
        } catch (ClassCastException e) {
            log.error("Type Casting Exception 발생: {}", e.getMessage());
            return false;
        }
    }
}

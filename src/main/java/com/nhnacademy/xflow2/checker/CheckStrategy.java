package com.nhnacademy.xflow2.checker;

public interface CheckStrategy<T> {
    boolean check(T data);
}

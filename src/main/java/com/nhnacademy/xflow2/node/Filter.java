package com.nhnacademy.xflow2.node;

import org.json.JSONException;

import com.nhnacademy.xflow2.checker.Checker;
import com.nhnacademy.xflow2.message.Message;

import lombok.extern.slf4j.Slf4j;

/**
 * 데이터를 특정 조건에 따라 필터링하는 클래스입니다.
 * 입력된 데이터를 주어진 조건에 따라 걸러냅니다.
 */
@Slf4j
public class Filter<T extends Message> extends InputOutputNode<T, T> {
    private final Checker<?> checker;

    public Filter(int inputCount, int outputCount, Checker<?> checker) {
        super(inputCount, outputCount);
        this.checker = checker;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                T receiveMessage = tryGetMessage();

                if (checker.check(receiveMessage.getPayload())) {
                    output(0, receiveMessage);
                }

            } catch (InterruptedException e) {
                log.error("Thread Error: {}", e.getMessage());
                Thread.currentThread().interrupt();
            } catch (JSONException e) {
                log.error("JSON Error: {}", e.getMessage());
            }
        }
    }
}

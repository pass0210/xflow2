package com.nhnacademy.xflow2.checker;

import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PDUStrategy implements CheckStrategy<JSONObject> {
    private static final int MAX_REGISTER = 10000;

    private int address;
    private int dataCount;
    private int dataByteLength;
    private int totalLength;

    @Override
    public boolean check(JSONObject msg) {
        int functionCode = msg.getInt("functionCode");
        int[] data = ((int[]) msg.get("data"));

        address = msg.getInt("startAddress");

        if (functionCode == 3 || functionCode == 4) {
            return (data.length == 1 && addressCheck() && quantityCheck(data[0]));

        } else if (functionCode == 6) {
            return (data.length == 1 && addressCheck() && valueCheck(data[0]));

        } else if (functionCode == 16) {
            dataCount = msg.getInt("dataCount");
            dataByteLength = msg.getInt("dataByteLength");
            totalLength = msg.getInt("totalLength");

            if (dataCount > 0 && lengthCheck()) {
                for (int d : data) {
                    if (!valueCheck(d)) {
                        return false;
                    }
                }
                return true;
            }
        }
        log.error("지원하지 않는 function code");
        return false;

    }

    private boolean addressCheck() {
        if (address < 0 || address > MAX_REGISTER) {
            log.error("잘못된 address 값 : 음수거나 최대 레지스터 갯수를 초과함");
            return false;
        }
        return true;
    }

    private boolean valueCheck(int value) {
        if (value < 0) {
            log.error("minus value");
            return false;
        }
        return true;
    }

    private boolean lengthCheck() {
        if (addressCheck()) {
            if ((dataCount == dataByteLength / 2) && (dataByteLength + 13 == totalLength)) {
                return true;
            }
            log.error("잘못된 length");
        }
        return false;
    }

    private boolean quantityCheck(int quantity) {
        if (quantity < 0) {
            log.error("minus quantity");
        } else if (address + quantity > MAX_REGISTER) {
            log.error("레지스터 범위를 초과함");
            return false;
        }
        return true;
    }
}

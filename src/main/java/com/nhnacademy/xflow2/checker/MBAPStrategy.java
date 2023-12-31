package com.nhnacademy.xflow2.checker;

import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * MBAP(Modbus Application Protocol)에 대한 조건을 확인하는 전략 클래스입니다.
 * MBAP 프레임의 구조를 검증합니다.
 */
@Slf4j
public class MBAPStrategy implements CheckStrategy<JSONObject> {
    private static final int PROTOCOL_ID = 0;
    private static final int UNIT_ID = 1;

    @Override
    public boolean check(JSONObject msg) {
        int transactionId = msg.getInt("transactionId");
        int protocolId = msg.getInt("protocolId");
        int totalLength = msg.getInt("totalLength");
        int length = msg.getInt("length");
        int unitId = msg.getInt("unitId");

        if (!((totalLength >= 7) && ((6 + length) == totalLength))) {
            log.error("header가 형식에 맞지 않음.");
            return false;
        }

        if ((transactionId < 0)) {
            log.error("transactionId가 음수이거나 범위를 초과함.");
            return false;
        }

        if (protocolId != PROTOCOL_ID) {
            log.error("protocolId가 0이 아님.");
            return false;
        }

        if (unitId != UNIT_ID) {
            log.error("UnitId가 1이 아님.");
            return false;
        }

        return true;
    }
}

package com.nhnacademy.xflow2.checker;

import org.json.JSONObject;

import com.nhnacademy.xflow2.message.JSONWithSocketMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MBAPStraegy implements CheckStrategy<JSONWithSocketMessage>{
    private static final int PROTOCOL_ID = 0;
    private static final int UNIT_ID = 1;

    @Override
    public boolean check(JSONWithSocketMessage data) {
        JSONObject msg = data.getPayload();

        int transactionId = msg.getInt("transactionId");
        int protocolId = msg.getInt("protocolId");
        int totalLength = msg.getInt("totalLength");
        int length = msg.getInt("length");
        int unitId = msg.getInt("unitId");
        
        if(!((totalLength >= 7) && ((6 + length) == totalLength))){
            log.error("header가 형식에 맞지 않음.");
            return false;
        }

        if((transactionId < 0)){
            log.error("transactionId가 음수이거나 범위를 초과함.");
            return false;
        }

        if(protocolId != PROTOCOL_ID){
            log.error("protocolId가 0이 아님.");
            return false;
        }

        if(unitId != UNIT_ID){
            log.error("UnitId가 1이 아님.");
            return false;
        }

        return true;
    }
}

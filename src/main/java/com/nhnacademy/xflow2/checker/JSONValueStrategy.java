package com.nhnacademy.xflow2.checker;

import org.json.JSONObject;

import java.util.List;

/**
 * MBAP(Modbus Application Protocol)에 대한 조건을 확인하는 전략 클래스입니다.
 * MBAP 프레임의 구조를 검증합니다.
 */
public class JSONValueStrategy implements CheckStrategy<JSONObject> {

    private final String targetKey;
    private final List<String> valueList;
    private final List<String> subKeyList;

    public JSONValueStrategy(String targetKey, List<String> valueList, List<String> subKeyList) {
        this.targetKey = targetKey;
        this.valueList = valueList;
        this.subKeyList = subKeyList;
    }

    @Override
    public boolean check(JSONObject object) {

        for (String subKey : subKeyList) {
            object = object.getJSONObject(subKey);
        }

        for (String value : valueList) {
            if (object.optString(targetKey).contains(value))
                return true;
        }

        return false;
    }
}

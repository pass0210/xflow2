package com.nhnacademy.xflow2.checker;

/**
 * JSON 데이터의 Key에 대한 조건을 확인하는 전략 클래스입니다.
 * 특정 Key 또는 Key의 하위 키에 대한 검증을 수행합니다.
 */
import java.util.List;

import org.json.JSONObject;

public class JSONKeyStrategy implements CheckStrategy<JSONObject> {
    private final List<String> targetKeyList;
    private final List<String> subKeyList;

    public JSONKeyStrategy(List<String> targetKeyList, List<String> subKeyList) {
        this.targetKeyList = targetKeyList;
        this.subKeyList = subKeyList;
    }

    @Override
    public boolean check(JSONObject object) {
        for (String subKey : subKeyList) {
            object = object.getJSONObject(subKey);
        }

        for (String key : targetKeyList) {
            if (object.has(key)) {
                return true;
            }
        }
        return false;
    }
}

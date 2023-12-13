package com.nhnacademy.xflow2.checker;

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

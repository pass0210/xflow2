package com.nhnacademy.xflow2.splitter;

import java.util.Map;

import org.json.JSONObject;

/**
 * JSON 데이터를 입력으로 받아 특정 키("object")의 하위 맵을 추출하는 클래스입니다.
 * 이 맵을 반환하여 데이터를 분리 및 추출하는 역할을 수행합니다.
 */
public class TypeSplitter {
    
    public Map<String, Object> generate(JSONObject object){
        return object.getJSONObject("object").toMap();
    }

}

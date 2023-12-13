package com.nhnacademy.xflow2.node;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import com.nhnacademy.xflow2.message.JSONMessage;
import com.nhnacademy.xflow2.splitter.TypeSplitter;

import java.util.Map;
import java.util.Map.Entry;

@Slf4j
public class ObjectGenerator extends InputOutputNode<JSONMessage, JSONMessage> {
    private final TypeSplitter typeSplitter;

    public ObjectGenerator(int inputCount, int outputCount) {
        super(inputCount, outputCount);
        typeSplitter = new TypeSplitter();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                JSONMessage message = tryGetMessage();
                JSONObject object = message.getPayload();

                String devEui = object.getJSONObject("deviceInfo").getString("devEui");
                Map<String, Object> sensorInfo = typeSplitter.generate(object); // 센서 값 받기

                for (Entry<String, Object> entrySet : sensorInfo.entrySet()) {
                    JSONObject o = new JSONObject();
                    o.put("sensorId", devEui + "-" + entrySet.getKey());
                    o.put("value", entrySet.getValue());
                    output(0, new JSONMessage(o));
                }
            } catch (InterruptedException e) {
                log.error("Thread Error : {}", e.getMessage());
                Thread.currentThread().interrupt();
            } catch (JSONException e) {
                log.warn("JSON Error: {}", e.getMessage());
            }
        }
    }
}

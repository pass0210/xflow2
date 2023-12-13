package com.nhnacademy.xflow2;

import org.json.JSONObject;

import com.nhnacademy.xflow2.message.JSONMessage;
import com.nhnacademy.xflow2.node.InputOutputNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttMessageGenerator extends InputOutputNode<JSONMessage, JSONMessage>{
    CommonsTopicGenerator topicGenerator;

    protected MqttMessageGenerator(int inputCount, int outputCount) {
        super(inputCount, outputCount);
        topicGenerator = new CommonsTopicGenerator();
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                Object object = tryGetMessage();
                if (object instanceof JSONMessage) {
                    JSONObject message = ((JSONMessage) object).getPayload();

                    JSONObject o = new JSONObject();
                    o.put("topic", topicGenerator.generate(message).toString());
                    o.put("time", System.currentTimeMillis() / 1000L);
                    o.put("value", (message.getDouble("value")));
                    log.info(o.toString());
                    output(0, new JSONMessage(o));
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
    
    
}

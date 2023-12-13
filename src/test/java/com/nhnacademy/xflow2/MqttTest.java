package com.nhnacademy.xflow2;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import com.nhnacademy.xflow2.checker.Checker;
import com.nhnacademy.xflow2.checker.JSONKeyStrategy;
import com.nhnacademy.xflow2.checker.JSONValueStrategy;
import com.nhnacademy.xflow2.client.MqttClientManager;
import com.nhnacademy.xflow2.message.JSONMessage;
import com.nhnacademy.xflow2.node.Filter;
import com.nhnacademy.xflow2.node.MqttMessageGenerator;
import com.nhnacademy.xflow2.node.MqttPublisher;
import com.nhnacademy.xflow2.node.MqttSubscriber;
import com.nhnacademy.xflow2.node.ObjectGenerator;
import com.nhnacademy.xflow2.node.RuleEngine;

public class MqttTest {
    public static void main(String[] args) {
        MqttSubscriber mqttSubscriber = new MqttSubscriber(1, MqttClientManager.getClient("tcp://ems.nhnacademy.com"),
                "application/#");

        List<String> keyFilterTargetKeyList = new ArrayList<>();
        List<String> keyFilterSubKeyList = new ArrayList<>();
        keyFilterTargetKeyList.add("deviceInfo");
        Checker<JSONObject> keyFilterChecker = new Checker<>(
                new JSONKeyStrategy(keyFilterTargetKeyList, keyFilterSubKeyList));
        Filter<JSONMessage> keyFilter = new Filter<>(1, 1, keyFilterChecker);

        List<String> appNameFilterValueList = new ArrayList<>();
        appNameFilterValueList.add("NHNAcademyEMS");
        List<String> appNameFilterSubKeyList = new ArrayList<>();
        appNameFilterSubKeyList.add("deviceInfo");
        Checker<JSONObject> appNameFilterChecker = new Checker<>(
                new JSONValueStrategy("applicationName", appNameFilterValueList, appNameFilterSubKeyList));
        Filter<JSONMessage> appNameFilter = new Filter<>(1, 1, appNameFilterChecker);

        ObjectGenerator objectGenerator = new ObjectGenerator(1, 1);

        List<String> sensorTypeFilterValuList = new ArrayList<>();
        List<String> sensorTypeFilterSubKeyList = new ArrayList<>();
        sensorTypeFilterValuList.add("temperature");
        sensorTypeFilterValuList.add("humidity");
        sensorTypeFilterValuList.add("illumination");
        Checker<JSONObject> sensorTypeFilterChecker = new Checker<>(
                new JSONValueStrategy("sensorId", sensorTypeFilterValuList, sensorTypeFilterSubKeyList));
        Filter<JSONMessage> sensorTypeFilter = new Filter<>(1, 1, sensorTypeFilterChecker);

        RuleEngine ruleEngine = null;
        try {
            JSONParser parser = new JSONParser();
            Reader reader = new FileReader("./src/main/java/com/nhnacademy/xflow2/database.json");
            JSONObject jsonObject = new JSONObject(parser.parse(reader).toString());
            ruleEngine = new RuleEngine(1, 1, jsonObject);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        MqttMessageGenerator mqttMessageGenerator = new MqttMessageGenerator(1, 1);

        MqttPublisher mqttPublisher = new MqttPublisher(1, MqttClientManager.getClient("tcp://localhost"));

        mqttSubscriber.connect(0, keyFilter.getInputPort(0));
        keyFilter.connect(0, appNameFilter.getInputPort(0));
        appNameFilter.connect(0, objectGenerator.getInputPort(0));
        objectGenerator.connect(0, sensorTypeFilter.getInputPort(0));
        sensorTypeFilter.connect(0, ruleEngine.getInputPort(0));
        ruleEngine.connect(0, mqttMessageGenerator.getInputPort(0));
        mqttMessageGenerator.connect(0, mqttPublisher.getInputPort(0));

        mqttSubscriber.start();
        keyFilter.start();
        appNameFilter.start();
        objectGenerator.start();
        sensorTypeFilter.start();
        ruleEngine.start();
        mqttMessageGenerator.start();
        mqttPublisher.start();
    }
}

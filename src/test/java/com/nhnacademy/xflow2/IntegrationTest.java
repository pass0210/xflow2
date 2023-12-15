package com.nhnacademy.xflow2;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.nhnacademy.xflow2.checker.Checker;
import com.nhnacademy.xflow2.checker.JSONValueStrategy;
import com.nhnacademy.xflow2.checker.JSONKeyStrategy;
import com.nhnacademy.xflow2.checker.MBAPStrategy;
import com.nhnacademy.xflow2.checker.PDUStrategy;
import com.nhnacademy.xflow2.client.ModbusClientManager;
import com.nhnacademy.xflow2.client.MqttClientManager;
import com.nhnacademy.xflow2.message.JSONMessage;
import com.nhnacademy.xflow2.message.JSONWithSocketMessage;
import com.nhnacademy.xflow2.node.ByteParser;
import com.nhnacademy.xflow2.node.Filter;
import com.nhnacademy.xflow2.node.FunctionCodeSelection;
import com.nhnacademy.xflow2.node.ModbusClient;
import com.nhnacademy.xflow2.node.ModbusIn;
import com.nhnacademy.xflow2.node.ModbusMapper;
import com.nhnacademy.xflow2.node.ModbusOut;
import com.nhnacademy.xflow2.node.MqttMessageGenerator;
import com.nhnacademy.xflow2.node.MqttPublisher;
import com.nhnacademy.xflow2.node.MqttSubscriber;
import com.nhnacademy.xflow2.node.ObjectGenerator;
import com.nhnacademy.xflow2.node.RegisterReader;
import com.nhnacademy.xflow2.node.RegisterUpdater;
import com.nhnacademy.xflow2.node.RegisterWriter;
import com.nhnacademy.xflow2.node.ResponseGenerator;
import com.nhnacademy.xflow2.node.RuleEngine;

public class IntegrationTest {
    public static void main(String[] args) {
        try {
            // TODO 1 MQTT 선언(박상원) 1명
            MqttSubscriber mqttSubscriber = new MqttSubscriber(1,
                    MqttClientManager.getClient("tcp://ems.nhnacademy.com"),
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

            JSONParser ruleEngineParser = new JSONParser();
            Reader RuleEngineReader = new FileReader("./src/main/java/com/nhnacademy/xflow2/database.json");
            JSONObject ruleEngineJsonObject = new JSONObject(ruleEngineParser.parse(RuleEngineReader).toString());
            RuleEngine ruleEngine = new RuleEngine(1, 2, ruleEngineJsonObject);

            MqttMessageGenerator mqttMessageGenerator = new MqttMessageGenerator(1, 1);

            MqttPublisher mqttPublisher = new MqttPublisher(1, MqttClientManager.getClient("tcp://localhost"));

            // TODO 2 MODBUS 선언(김재혁) 1명
            ModbusClient modbusClient = new ModbusClient(1, ModbusClientManager.getSocket("localhost", 502));

            JSONParser modbusParser = new JSONParser();
            Reader modbusReader = new FileReader("./src/main/java/com/nhnacademy/xflow2/key-mapping-table.json");
            JSONObject modbusJsonObject = new JSONObject(modbusParser.parse(modbusReader).toString());
            ModbusMapper modbusMapper = new ModbusMapper(1, 1, modbusJsonObject);

            RegisterUpdater registerUpdater = new RegisterUpdater(1);

            // TODO 3 MODBUS Server 선언(이은지, 남가형) 2명
            ModbusIn modbusIn = new ModbusIn(1, new ServerSocket(13245));

            ByteParser byteParser = new ByteParser(1, 1);

            MBAPStrategy mbapStrategy = new MBAPStrategy();
            Checker<JSONObject> mbapChecker = new Checker<>(mbapStrategy);
            Filter<JSONWithSocketMessage> mbapFilter = new Filter<>(1, 1, mbapChecker);

            PDUStrategy pduStrategy = new PDUStrategy();
            Checker<JSONObject> pduChecker = new Checker<>(pduStrategy);
            Filter<JSONWithSocketMessage> pduFilter = new Filter<>(1, 1, pduChecker);

            FunctionCodeSelection functionCodeSelection = new FunctionCodeSelection(1, 2);

            RegisterWriter registerWriter = new RegisterWriter(1, 1);
            RegisterReader registerReader = new RegisterReader(1, 1);

            ResponseGenerator responseGenerator = new ResponseGenerator(1, 1);

            ModbusOut modbusOut = new ModbusOut(1);

            // TODO 4 연결
            // mqtt
            mqttSubscriber.connect(0, keyFilter.getInputPort(0));
            keyFilter.connect(0, appNameFilter.getInputPort(0));
            appNameFilter.connect(0, objectGenerator.getInputPort(0));
            objectGenerator.connect(0, sensorTypeFilter.getInputPort(0));
            sensorTypeFilter.connect(0, ruleEngine.getInputPort(0));
            ruleEngine.connect(0, mqttMessageGenerator.getInputPort(0));
            mqttMessageGenerator.connect(0, mqttPublisher.getInputPort(0));

            // modbus
            modbusClient.connect(0, modbusMapper.getInputPort(0));
            modbusMapper.connect(0, ruleEngine.getInputPort(0));
            ruleEngine.connect(1, registerUpdater.getInputPort(0));

            // modbusServer
            modbusIn.connect(0, byteParser.getInputPort(0));
            byteParser.connect(0, mbapFilter.getInputPort(0));
            mbapFilter.connect(0, pduFilter.getInputPort(0));
            pduFilter.connect(0, functionCodeSelection.getInputPort(0));
            functionCodeSelection.connect(0, registerWriter.getInputPort(0));
            functionCodeSelection.connect(1, registerReader.getInputPort(0));
            registerWriter.connect(0, responseGenerator.getInputPort(0));
            registerReader.connect(0, responseGenerator.getInputPort(0));
            responseGenerator.connect(0, modbusOut.getInputPort(0));

            // TODO 5 실행
            mqttSubscriber.start();
            keyFilter.start();
            appNameFilter.start();
            objectGenerator.start();
            sensorTypeFilter.start();
            ruleEngine.start();
            mqttMessageGenerator.start();
            mqttPublisher.start();

            modbusClient.start();
            modbusMapper.start();
            registerUpdater.start();

            modbusIn.start();
            byteParser.start();
            mbapFilter.start();
            pduFilter.start();
            functionCodeSelection.start();
            registerWriter.start();
            registerReader.start();
            responseGenerator.start();
            modbusOut.start();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

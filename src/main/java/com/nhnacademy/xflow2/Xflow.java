package com.nhnacademy.xflow2;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.nhnacademy.xflow2.checker.Checker;
import com.nhnacademy.xflow2.checker.JSONKeyStrategy;
import com.nhnacademy.xflow2.checker.JSONValueStrategy;
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

import lombok.extern.slf4j.Slf4j;

/**
 * Hello world!
 *
 */
@Slf4j
public class Xflow {
    private static JSONObject setting;

    private static final String SETTING_FILE_PATH = "./src/main/java/com/nhnacademy/xflow2/setting.json";
    private static final String PROPERTIES_PATH = "./src/main/java/com/nhnacademy/xflow2/properties.json";
    private static final String PARAMETERS = "parameters";
    private static final String CHECKER = "Checker";
    private static final String KEY_FILTER = "KeyFilter";
    private static final String APP_NAME_FILTER = "AppNameFilter";
    private static final String SENSOR_TYPE_FILTER = "SensorTypeFilter";

    private static final String SERVER_URI = "serverURI";
    private static final String MQTT_SUBSCRIBER = "MqttSubscriber";
    private static final String KEY_LIST = "keyList";
    private static final String SUB_KEY_LIST = "subKeyList";
    private static final String TARGET_KEY = "targetKey";
    private static final String VALUE_LIST = "valueList";
    private static final String RULE_ENGINE = "RuleEngine";
    private static final String KEY_MAPPING_TABLE = "key-mapping-table";
    private static final String DATA_BASE = "database";
    private static final String MODBUS_CLIENT = "modbusClient";
    private static final String MODBUS_MAPPER = "modbusMapper";
    private static final String MODBUS_IN = "modbusIn";
    private static final String SENSORTYPE_FILTER = "SensorTypeFilter";

    public static void main(String[] args) {
        try {
            setting = jsonFileReader(PROPERTIES_PATH);
            setCommendLineArgument(args);
            run();
        } catch (ParseException e) {
            log.error("parse error:{}", e.getMessage());
        } catch (IOException e) {
            log.error("I/O error: {}", e.getMessage());
        }
    }

    // JSON 파일 읽기
    public static JSONObject jsonFileReader(String filePath) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONParser parser = new JSONParser();
            Reader reader = new FileReader(filePath);
            jsonObject = new JSONObject(parser.parse(reader).toString());

        } catch (JSONException e) {
            log.error("JSON Error: {}", e.getMessage());
        } catch (FileNotFoundException e) {
            log.error("File Error: {}", e.getMessage());
        } catch (IOException e) {
            log.error("IO Error: {}", e.getMessage());
        } catch (ParseException e) {
            log.error("parser Error: {}", e.getMessage());
        }
        return jsonObject;
    }

    // commendLine Argument 설정
    private static void setCommendLineArgument(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("an", "an", true, "filter app name");
        options.addOption("s", true, "filter sensor type");
        options.addOption("c", false, "use json.setting");

        CommandLineParser cmdParser = new DefaultParser();

        try {
            CommandLine cmd = cmdParser.parse(options, args);
            hasOption(cmd);
        } catch (org.apache.commons.cli.ParseException e) {
            log.error("setCommandLineArguments Error : {}", e.getMessage());
        }
    }

    private static void hasOption(CommandLine cmd) {
        if (cmd.hasOption("c")) {
            JSONObject settingJson = jsonFileReader(SETTING_FILE_PATH);
            setting(settingJson);
        }

        if (cmd.hasOption("an") && cmd.getOptionValue("an") != null) {
            String[] anSplit = cmd.getOptionValue("an").split(",");

            setting.getJSONObject(APP_NAME_FILTER).put(VALUE_LIST, new JSONArray(Arrays.asList(anSplit)));
        }
        if (cmd.hasOption("s") && cmd.getOptionValue("s") != null) {
            String[] sSplit = cmd.getOptionValue("s").split(",");

            setting.getJSONObject(SENSOR_TYPE_FILTER).put(VALUE_LIST, new JSONArray(Arrays.asList(sSplit)));
        }
    }

    private static void setting(JSONObject settingJson) {
        JSONArray keyFilterKeyList = settingJson.getJSONObject(KEY_FILTER)
                .getJSONObject(PARAMETERS)
                .getJSONObject(CHECKER)
                .getJSONObject(PARAMETERS).getJSONArray(KEY_LIST);
        JSONArray keyFilterSubKeyList = settingJson.getJSONObject(KEY_FILTER)
                .getJSONObject(PARAMETERS)
                .getJSONObject(CHECKER)
                .getJSONObject(PARAMETERS).getJSONArray(SUB_KEY_LIST);

        JSONArray appNameFilterValueList = settingJson.getJSONObject(APP_NAME_FILTER)
                .getJSONObject(PARAMETERS)
                .getJSONObject(CHECKER)
                .getJSONObject(PARAMETERS)
                .getJSONArray(VALUE_LIST);
        JSONArray appNameFilterSubKeyList = settingJson.getJSONObject(APP_NAME_FILTER)
                .getJSONObject(PARAMETERS)
                .getJSONObject(CHECKER)
                .getJSONObject(PARAMETERS)
                .getJSONArray(SUB_KEY_LIST);

        JSONArray sensorTypeFilterValueList = settingJson.getJSONObject(SENSOR_TYPE_FILTER)
                .getJSONObject(PARAMETERS)
                .getJSONObject(CHECKER)
                .getJSONObject(PARAMETERS)
                .getJSONArray(VALUE_LIST);
        JSONArray sensorTypeFilterSubKeyList = settingJson.getJSONObject(SENSOR_TYPE_FILTER)
                .getJSONObject(PARAMETERS)
                .getJSONObject(CHECKER)
                .getJSONObject(PARAMETERS)
                .getJSONArray(SUB_KEY_LIST);

        String mqttInServerURI = settingJson.getJSONObject("mqttIn")
                .getJSONObject(PARAMETERS)
                .getString(SERVER_URI);
        String mqttOutServerURI = settingJson.getJSONObject("mqttOut")
                .getJSONObject(PARAMETERS)
                .getString(SERVER_URI);
        String appNameFilterTargetKey = settingJson.getJSONObject(APP_NAME_FILTER)
                .getJSONObject(PARAMETERS)
                .getJSONObject(CHECKER)
                .getJSONObject(PARAMETERS)
                .getString(TARGET_KEY);
        String sensorTypeFilterTargetKey = settingJson.getJSONObject(SENSOR_TYPE_FILTER)
                .getJSONObject(PARAMETERS)
                .getJSONObject(CHECKER)
                .getJSONObject(PARAMETERS)
                .getString(TARGET_KEY);
        String ruleEngineDatabase = settingJson.getJSONObject(RULE_ENGINE).getJSONObject(PARAMETERS)
                .getString(DATA_BASE);
        String modbusClientServerAddress = settingJson.getJSONObject(MODBUS_CLIENT).getJSONObject(PARAMETERS)
                .getString("serveraddress");
        String modbusClientPort = settingJson.getJSONObject(MODBUS_CLIENT).getJSONObject(PARAMETERS)
                .getString("port");
        String modbusMapperKeyMappingTable = settingJson.getJSONObject(MODBUS_MAPPER).getJSONObject(PARAMETERS)
                .getString(KEY_MAPPING_TABLE);
        String modbusInPort = settingJson.getJSONObject(MODBUS_IN).getJSONObject(PARAMETERS).getString("port");

        setting.getJSONObject(MQTT_SUBSCRIBER).put(SERVER_URI, mqttInServerURI);
        setting.getJSONObject(KEY_FILTER).put(KEY_LIST, keyFilterKeyList);
        setting.getJSONObject(KEY_FILTER).put(SUB_KEY_LIST, keyFilterSubKeyList);

        setting.getJSONObject(APP_NAME_FILTER).put(VALUE_LIST, appNameFilterValueList);
        setting.getJSONObject(APP_NAME_FILTER).put(TARGET_KEY, appNameFilterTargetKey);
        setting.getJSONObject(APP_NAME_FILTER).put(SUB_KEY_LIST, appNameFilterSubKeyList);

        setting.getJSONObject(SENSOR_TYPE_FILTER).put(VALUE_LIST, sensorTypeFilterValueList);
        setting.getJSONObject(SENSOR_TYPE_FILTER).put(TARGET_KEY, sensorTypeFilterTargetKey);
        setting.getJSONObject(SENSOR_TYPE_FILTER).put(SUB_KEY_LIST, sensorTypeFilterSubKeyList);

        setting.getJSONObject("MqttPublisher").put(SERVER_URI, mqttOutServerURI);
        setting.getJSONObject(RULE_ENGINE).put(DATA_BASE, ruleEngineDatabase);
        setting.getJSONObject(MODBUS_CLIENT).put("serverAddress", modbusClientServerAddress);
        setting.getJSONObject(MODBUS_CLIENT).put("port", modbusClientPort);
        setting.getJSONObject(MODBUS_MAPPER).put(KEY_MAPPING_TABLE, modbusMapperKeyMappingTable);
        setting.getJSONObject(MODBUS_IN).put("port", modbusInPort);
    }

    // 노드 생성/연결/실행
    private static void run() throws JSONException, IOException {
        MqttSubscriber mqttSubscriber = new MqttSubscriber(1,
                MqttClientManager.getClient(setting.getJSONObject(MQTT_SUBSCRIBER).optString(SERVER_URI)),
                setting.getJSONObject(MQTT_SUBSCRIBER).optString("topicFilter"));

        JSONKeyStrategy jsonKeyStrategy = new JSONKeyStrategy(
                (getValueList(setting.getJSONObject(KEY_FILTER).getJSONArray(KEY_LIST))),
                (getValueList(setting.getJSONObject(KEY_FILTER).getJSONArray(SUB_KEY_LIST))));
        Checker<JSONObject> keyFilterChecker = new Checker<>(jsonKeyStrategy);
        Filter<JSONMessage> keyFilter = new Filter<>(1, 1, keyFilterChecker);

        JSONValueStrategy jsonValueStrategy = new JSONValueStrategy(
                setting.getJSONObject(APP_NAME_FILTER).optString(TARGET_KEY),
                getValueList(setting.getJSONObject(APP_NAME_FILTER).getJSONArray(VALUE_LIST)),
                getValueList(setting.getJSONObject(APP_NAME_FILTER).getJSONArray(SUB_KEY_LIST)));
        Checker<JSONObject> appNameChecker = new Checker<>(jsonValueStrategy);
        Filter<JSONMessage> appNameFilter = new Filter<>(1, 1, appNameChecker);

        ObjectGenerator objectGenerator = new ObjectGenerator(1, 1);

        jsonValueStrategy = new JSONValueStrategy(setting.getJSONObject(SENSORTYPE_FILTER).optString(TARGET_KEY),
                getValueList(setting.getJSONObject(SENSORTYPE_FILTER).getJSONArray(VALUE_LIST)),
                getValueList(setting.getJSONObject(SENSORTYPE_FILTER).getJSONArray(SUB_KEY_LIST)));
        Checker<JSONObject> sensorTypeFilterChecker = new Checker<>(jsonValueStrategy);
        Filter<JSONMessage> sensorTypeFilter = new Filter<>(1, 1, sensorTypeFilterChecker);

        RuleEngine ruleEngine = new RuleEngine(1, 2,
                jsonFileReader(setting.getJSONObject(RULE_ENGINE).getString(DATA_BASE)));

        MqttMessageGenerator mqttMessageGenerator = new MqttMessageGenerator(1, 1);

        MqttPublisher mqttPublisher = new MqttPublisher(1,
                MqttClientManager.getClient(setting.getJSONObject("MqttPublisher").optString(SERVER_URI)));

        ModbusClient modbusClient = new ModbusClient(1, ModbusClientManager
                .getSocket(setting.getJSONObject(MODBUS_CLIENT).optString("serverAddress"),
                        setting.getJSONObject(MODBUS_CLIENT).optInt("port")));

        ModbusMapper modbusMapper = new ModbusMapper(1, 1,
                jsonFileReader(setting.getJSONObject(MODBUS_MAPPER).getString(KEY_MAPPING_TABLE)));

        RegisterUpdater registerUpdater = new RegisterUpdater(1);

        ModbusIn modbusIn = new ModbusIn(1, new ServerSocket(setting.getJSONObject(MODBUS_IN).optInt("port")));

        ByteParser byteParser = new ByteParser(1, 1);

        Filter<JSONWithSocketMessage> mbapFilter = new Filter<>(1, 1, new Checker<>(new MBAPStrategy()));

        Filter<JSONWithSocketMessage> pduFilter = new Filter<>(1, 1, new Checker<>(new PDUStrategy()));

        FunctionCodeSelection functionCodeSelection = new FunctionCodeSelection(1, 2);

        RegisterWriter registerWriter = new RegisterWriter(1, 1);

        RegisterReader registerReader = new RegisterReader(1, 1);

        ResponseGenerator responseGenerator = new ResponseGenerator(1, 1);

        ModbusOut modbusOut = new ModbusOut(1);

        mqttSubscriber.connect(0, keyFilter.getInputPort(0));
        keyFilter.connect(0, appNameFilter.getInputPort(0));
        appNameFilter.connect(0, objectGenerator.getInputPort(0));
        objectGenerator.connect(0, sensorTypeFilter.getInputPort(0));
        sensorTypeFilter.connect(0, ruleEngine.getInputPort(0));
        ruleEngine.connect(0, mqttMessageGenerator.getInputPort(0));
        mqttMessageGenerator.connect(0, mqttPublisher.getInputPort(0));

        modbusClient.connect(0, modbusMapper.getInputPort(0));
        modbusMapper.connect(0, ruleEngine.getInputPort(0));
        ruleEngine.connect(1, registerUpdater.getInputPort(0));

        modbusIn.connect(0, byteParser.getInputPort(0));
        byteParser.connect(0, mbapFilter.getInputPort(0));
        mbapFilter.connect(0, pduFilter.getInputPort(0));
        pduFilter.connect(0, functionCodeSelection.getInputPort(0));
        functionCodeSelection.connect(0, registerWriter.getInputPort(0));
        functionCodeSelection.connect(1, registerReader.getInputPort(0));
        registerWriter.connect(0, responseGenerator.getInputPort(0));
        registerReader.connect(0, responseGenerator.getInputPort(0));
        responseGenerator.connect(0, modbusOut.getInputPort(0));

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
    }

    private static List<String> getValueList(JSONArray jsonArray) {
        List<String> resultList = new ArrayList<>();

        for (Object element : jsonArray) {
            resultList.add((String) element);
        }

        return resultList;
    }
}

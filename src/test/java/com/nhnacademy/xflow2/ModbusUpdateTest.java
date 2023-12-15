package com.nhnacademy.xflow2;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.nhnacademy.xflow2.client.ModbusClientManager;
import com.nhnacademy.xflow2.node.ModbusClient;
import com.nhnacademy.xflow2.node.ModbusMapper;
import com.nhnacademy.xflow2.node.RegisterUpdater;
import com.nhnacademy.xflow2.node.RuleEngine;

public class ModbusUpdateTest {
    public static void main(String[] args) {
        try {
            ModbusClient modbusClient = new ModbusClient(1, ModbusClientManager.getSocket("localhost", 502));

            JSONParser parser = new JSONParser();
            Reader reader = new FileReader("./src/main/java/com/nhnacademy/xflow2/key-mapping-table.json");
            JSONObject jsonObject = new JSONObject(parser.parse(reader).toString());
            ModbusMapper modbusMapper = new ModbusMapper(1, 1, jsonObject);

            parser = new JSONParser();
            Reader modbusReader = new FileReader("./src/main/java/com/nhnacademy/xflow2/database.json");
            JSONObject ruleEngineJsonObject = new JSONObject(parser.parse(modbusReader).toString());
            RuleEngine ruleEngine = new RuleEngine(1, 1, ruleEngineJsonObject);

            RegisterUpdater registerUpdater = new RegisterUpdater(1);

            modbusClient.connect(0, modbusMapper.getInputPort(0));
            modbusMapper.connect(0, ruleEngine.getInputPort(0));
            ruleEngine.connect(0, registerUpdater.getInputPort(0));

            modbusClient.start();
            modbusMapper.start();
            ruleEngine.start();
            registerUpdater.start();
        } catch (IOException | JSONException | ParseException e) {
            e.printStackTrace();
        }
    }
}

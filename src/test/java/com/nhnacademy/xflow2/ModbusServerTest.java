package com.nhnacademy.xflow2;

import java.io.IOException;
import java.net.ServerSocket;

import org.json.JSONObject;

import com.nhnacademy.xflow2.checker.Checker;
import com.nhnacademy.xflow2.checker.MBAPStrategy;
import com.nhnacademy.xflow2.checker.PDUStrategy;
import com.nhnacademy.xflow2.message.JSONWithSocketMessage;
import com.nhnacademy.xflow2.node.ByteParser;
import com.nhnacademy.xflow2.node.Filter;
import com.nhnacademy.xflow2.node.ModbusIn;
import com.nhnacademy.xflow2.node.ModbusOut;
import com.nhnacademy.xflow2.node.RegisterReader;
import com.nhnacademy.xflow2.node.RegisterWriter;
import com.nhnacademy.xflow2.node.ResponseGenerator;

import lombok.extern.slf4j.Slf4j;

import com.nhnacademy.xflow2.node.FunctionCodeSelection;

@Slf4j
public class ModbusServerTest {
    public static void main(String[] args) {
        try {
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

            modbusIn.connect(0, byteParser.getInputPort(0));
            byteParser.connect(0, mbapFilter.getInputPort(0));
            mbapFilter.connect(0, pduFilter.getInputPort(0));
            pduFilter.connect(0, functionCodeSelection.getInputPort(0));
            functionCodeSelection.connect(0, registerWriter.getInputPort(0));
            functionCodeSelection.connect(1, registerReader.getInputPort(0));
            registerWriter.connect(0, responseGenerator.getInputPort(0));
            registerReader.connect(0, responseGenerator.getInputPort(0));
            responseGenerator.connect(0, modbusOut.getInputPort(0));

            modbusIn.start();
            byteParser.start();
            mbapFilter.start();
            pduFilter.start();
            functionCodeSelection.start();
            registerWriter.start();
            registerReader.start();
            responseGenerator.start();
            modbusOut.start();

        } catch (IOException e) {
            log.error("error: {}", e.getMessage());
        }

    }
}

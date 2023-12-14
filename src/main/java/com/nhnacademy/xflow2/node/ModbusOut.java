package com.nhnacademy.xflow2.node;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import com.nhnacademy.xflow2.message.ByteWithSocketMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModbusOut extends InputNode<ByteWithSocketMessage> {

    protected ModbusOut(int inputCount) throws IOException {
        super(inputCount);

    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ByteWithSocketMessage message = tryGetMessage();
                byte[] byteArray = message.getPayload();
                Socket socket = message.getSocket();
                socket.getOutputStream().write(byteArray);
                socket.getOutputStream().flush();
                socket.close();

            } catch (InterruptedException e) {

                log.error(e.getMessage());
                Thread.currentThread().interrupt();

            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}

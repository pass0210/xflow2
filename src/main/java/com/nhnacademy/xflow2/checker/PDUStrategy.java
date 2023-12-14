package com.nhnacademy.xflow2.checker;

import com.nhnacademy.xflow2.message.ByteWithSocketMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PDUStrategy implements CheckStrategy<ByteWithSocketMessage>{
    private static final int MAX_REGISTER = 10000;
    private static final int MAX_VALUE = 65536;

    @Override
    public boolean check(ByteWithSocketMessage data) {
        byte[] msg = data.getPayload();
        int functionCode = msg[7];
        int address = msg[8] << 8 | (msg[9] & 0xff);
        
        if (functionCode == 3 || functionCode == 4) {
            int quantity = msg[10] << 8 | (msg[11] & 0xff);

            return (addressCheck(address) && quantityCheck(address, quantity));

        } else if(functionCode == 6) {
            int value = msg[10] << 8 | (msg[11] & 0xff);

            return (addressCheck(address) && valueCheck(value));

        } else if(functionCode == 16){
            int quantity = msg[10] << 8 | (msg[11] & 0xff);
            int byteCount = msg[12];
            if (lengthCheck(msg, address, quantity, byteCount)){
                for (int i=0; i< byteCount; i+=2){
                    if (!valueCheck(msg[13+i] << 8 | (msg[14+i] & 0xff))){
                        return false;
                    }
                }
                return true;
            }
        }
        log.error("지원하지 않는 function code");
        return false;

    }
    
    private boolean lengthCheck(byte[] msg, int address, int quantity, int byteCount){
        if (addressCheck(address) && quantityCheck(address, quantity)) {
            if((quantity*2 == byteCount) && (byteCount + 12 == msg.length)){
                return true;
            }
            log.error("잘못된 length");
        }
        return false;
    }

    private boolean addressCheck(int address){
        if (address < 0 || address>MAX_REGISTER){
            log.error("잘못된 address 값 : 음수거나 최대 레지스터 갯수를 초과함");
            return false;
        }
        return true;
    }
    
    private boolean valueCheck(int value){
        if (value < 0 || value > MAX_VALUE){
            log.error("잘못된 value 값 : 음수거나 최대 bit수를 초과함");
            return false;
        }
        return true;
    }

    private boolean quantityCheck(int startAddress, int quantity) {
        if (startAddress < 0 || startAddress > MAX_REGISTER) {
            log.error("잘못된 start address: 음수거나 최대 레지스터 갯수를 초과함");
        } else if (quantity<0 || quantity > MAX_REGISTER) {
            log.error("잘못된 quantity: 음수이거나 최대 레지스터 갯수를 초과함");
        } else if (startAddress + quantity > MAX_REGISTER){
            log.error("레지스터 범위를 초과함");
            return false;
        }
        return true;
    }
}

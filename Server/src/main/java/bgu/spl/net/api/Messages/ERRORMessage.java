package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

public class ERRORMessage implements Message {
    short Opcode = 11;
    short otherOptCode;
    public ERRORMessage(){otherOptCode = -1;}
    public ERRORMessage(short otherOptCode) {
        this.otherOptCode = otherOptCode;
    }

    public void setOtherOptCode(short otherOptCode) {
        this.otherOptCode = otherOptCode;
    }

    public short getOpcode(){
        return Opcode;
    }

    public short getOtherOptCode() {
        return otherOptCode;
    }
}
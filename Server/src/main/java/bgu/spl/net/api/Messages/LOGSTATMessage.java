package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

public class LOGSTATMessage implements Message {
    short Opcode = 7;

    public LOGSTATMessage() {
    }

    public short getOpcode(){
        return Opcode;
    }
}
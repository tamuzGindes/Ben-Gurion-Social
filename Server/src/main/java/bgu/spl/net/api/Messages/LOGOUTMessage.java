package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

public class LOGOUTMessage implements Message {
    short Opcode = 3;

    public LOGOUTMessage() {
    }

    public short getOpcode(){
        return Opcode;
    }
}
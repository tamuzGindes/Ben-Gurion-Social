package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

import java.nio.charset.StandardCharsets;

public class BLOCKMessage implements Message {
    short Opcode = 12;
    String userNameToBlock;

    public BLOCKMessage() {
    }

    public void setUserNameToBlock(String userNameToBlock) {
        this.userNameToBlock = userNameToBlock;
    }

    public BLOCKMessage(String userNameToBlock) {
        this.userNameToBlock = userNameToBlock;
    }
    public short getOpcode(){
        return Opcode;
    }

    public String getUserNameToBlock() {
        return userNameToBlock;
    }
    /////////////////////////////////////////////////////////////////////////
    public byte[] getUserNameToBlockBytes() {
        return userNameToBlock.getBytes(StandardCharsets.UTF_8);
    }
}
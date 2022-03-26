package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ACKMessage implements Message {
    short Opcode = 10;
    short otherOpcode;
    List<short[]> stats;
    String msg;

    public ACKMessage() {
        otherOpcode = -1;
    }
    public ACKMessage(short otherOpcode) {
        this.otherOpcode = otherOpcode;
    }
    public ACKMessage(short otherOpcode, List<short[]> stats) {
        this.otherOpcode = otherOpcode;
        this.stats = stats;
    }
    public ACKMessage(short otherOpcode,String msg) {
        this.otherOpcode = otherOpcode;
        this.msg = msg;
    }
    public short getOpcode(){
        return Opcode;
    }


    public short getOtherOpcode() {
        return otherOpcode;
    }

    public void setOtherOpcode(short otherOpcode) {
        this.otherOpcode = otherOpcode;
    }

    public List<short[]> getStats() {
        return stats;
    }

    public void setStats(List<short[]> stats) {
        this.stats = stats;
    }

    public String getMsg() {
        return msg;
    }
    public byte[] getMsgBytes() {
        return msg.getBytes(StandardCharsets.UTF_8);
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

}
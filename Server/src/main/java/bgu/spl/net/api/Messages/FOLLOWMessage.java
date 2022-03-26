package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

import java.nio.charset.StandardCharsets;

public class FOLLOWMessage  implements Message {
    short Opcode = 4;
    byte follow = 0;
    String Username;

    public FOLLOWMessage() {
    }

    public FOLLOWMessage(byte follow, String username) {
        this.follow = follow;
        Username = username;
    }

    public void setFollow(byte follow) {
        this.follow = follow;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public byte getFollow() {
        return follow;
    }

    public String getUsername() {
        return Username;
    }
    public short getOpcode(){
        return Opcode;
    }


    /////////////////////////////////////////////////////////////////////////
    public byte[] getUsernameBytes() {
        return Username.getBytes(StandardCharsets.UTF_8);
    }

}
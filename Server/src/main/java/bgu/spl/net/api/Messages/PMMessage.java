package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

import java.nio.charset.StandardCharsets;

public class PMMessage implements Message {
    short Opcode = 6;
    String Username;
    String Content;
    String DateAndTime;
    public PMMessage(){}
    public PMMessage(String username, String content, String dateAndTime) {
        Username = username;
        Content = content;
        DateAndTime = dateAndTime;
    }

    public short getOpcode(){
        return Opcode;
    }

    public String getUsername() {
        return Username;
    }

    public String getContent() {
        return Content;
    }

    public String getDateAndTime() {
        return DateAndTime;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setContent(String content) {
        Content = content;
    }

    public void setDateAndTime(String dateAndTime) {
        DateAndTime = dateAndTime;
    }

    /////////////////////////////////////////////////////////////////////////
    public byte[] getUsernameBytes() {
        return Username.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getContentBytes() {
        return Content.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getDateAndTimeBytes() {
        return DateAndTime.getBytes(StandardCharsets.UTF_8);
    }

}
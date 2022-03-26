package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

import java.nio.charset.StandardCharsets;

public class NOTIFICATIONMessage implements Message {
    short Opcode = 9;
    byte NotificationType = 0;
    String PostingUser;
    String Content;
    public NOTIFICATIONMessage(){}
    public NOTIFICATIONMessage(byte notificationType, String postingUser, String content) {
        this.NotificationType = notificationType;
        this.PostingUser = postingUser;
        this.Content = content;
    }

    public short getOpcode(){
        return Opcode;
    }

    public byte getNotificationType() {
        return NotificationType;
    }

    public String getPostingUser() {
        return PostingUser;
    }

    public String getContent() {
        return Content;
    }

    public void setNotificationType(byte notificationType) {
        NotificationType = notificationType;
    }

    public void setPostingUser(String postingUser) {
        PostingUser = postingUser;
    }

    public void setContent(String content) {
        Content = content;
    }

    /////////////////////////////////////////////////////////////////////////
    public byte[] getPostingUserBytes() {
        return PostingUser.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getContentBytes() {
        return Content.getBytes(StandardCharsets.UTF_8);
    }


}
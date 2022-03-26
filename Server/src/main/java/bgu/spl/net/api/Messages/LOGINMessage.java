package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

import java.nio.charset.StandardCharsets;

public class LOGINMessage implements Message {
    short Opcode = 2;
    String Username;
    String password;
    byte Captcha;

    public LOGINMessage(String username, String password, byte captcha) {
        Username = username;
        this.password = password;
        Captcha = captcha;
    }

    public LOGINMessage() { }

    public void setUsername(String username) {
        Username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCaptcha(byte captcha) {
        Captcha = captcha;
    }

    public short getOpcode(){
        return Opcode;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return password;
    }

    public byte getCaptcha() {
        return Captcha;
    }

    /////////////////////////////////////////////////////////////////////////
    public byte[] getUsernameBytes() {
        return Username.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getPasswordBytes() {
        return password.getBytes(StandardCharsets.UTF_8);
    }

}
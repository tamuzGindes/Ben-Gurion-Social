package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class REGISTERMessage implements Message {
    short Opcode = 1;
    String Username;
    String Password;
    String Birthday;

    public REGISTERMessage() { }

    public REGISTERMessage(String username, String password, String birthday) {
        Username = username;
        Password = password;
        Birthday = birthday;
    }


    public short getOpcode(){
        return Opcode;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public String getBirthday() {
        return Birthday;
    }


    /////////////////////////////////////////////////////////////////////////
    public byte[] getUsernameBytes() {
        return Username.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getPasswordBytes() {
        return Password.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getBirthdayBytes() {
        return Birthday.getBytes(StandardCharsets.UTF_8);
    }
}

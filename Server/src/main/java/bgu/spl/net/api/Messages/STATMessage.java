package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class STATMessage implements Message {
    short Opcode = 8;
    List<String> usersNames;
    public STATMessage() { usersNames = new ArrayList<>();}

    public STATMessage(List<String> usersName) {
        this.usersNames = usersName;
    }

    public short getOpcode() {
        return Opcode;
    }

    public List<String> getUsersNames() {
        return usersNames;
    }

    //////////////////////////////////////
    public byte[][] getUsersNamesBytes() {
        byte[][] usersBytes = new byte[usersNames.size()][];
        int i = 0;
        for (String user : usersNames) {
            usersBytes[i] = user.getBytes(StandardCharsets.UTF_8);
            i++;
        }
        return usersBytes;
    }
    public void setUsersNames(String s) {
        String temp = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '|')
                temp += c;
            else{
                usersNames.add(temp);
                temp = "";
            }
        }
        usersNames.add(temp);
    }
}
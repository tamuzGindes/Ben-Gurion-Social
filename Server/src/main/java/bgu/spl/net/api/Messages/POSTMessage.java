package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class POSTMessage implements Message {
    short Opcode = 5;
    String Content;
    List<String> usersTags;

    public POSTMessage(){}

    public POSTMessage(String content) {
        setContent(content);
        setUsersTags(content);
    }

    public void setContent(String content) {
        Content = content;
    }

    public void setUsersTags(String content) {
        String [] splitted = content.split(" ");
        usersTags = new ArrayList<>();
        for(int i = 0 ; i < splitted.length ; i++){
            if(splitted[i].startsWith("@")){
                usersTags.add(splitted[i].substring(1));
            }
        }
    }

    public short getOpcode(){
        return Opcode;
    }

    public String getContent() {
        return Content;
    }

    public List<String> getUsersTags() {
        return usersTags;
    }

    /////////////////////////////////////////////////////////////////////////
    public byte[] getContentBytes() {
        return Content.getBytes(StandardCharsets.UTF_8);
    }
    public byte[][] getUsersTagsBytes() {
        byte[][] taggedUserNames = new byte[usersTags.size()][];
        Iterator iter = usersTags.stream().iterator();
        int i = 0;
        while(iter.hasNext()){
            taggedUserNames[i] = ((String)iter.next()).getBytes(StandardCharsets.UTF_8);
            i++;
        }
        return taggedUserNames;
    }


}
package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Message;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.Messages.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    short opt;
    Message message;
    byte endMessage;
    private byte[] bytes;
    private int zeroCount;
    private int bytesExpected;
    private int len;
    private byte one;

    public MessageEncoderDecoderImpl() {
        bytes = new byte[1 << 10];
        zeroCount = -1;
        bytesExpected = 0;
        len = 0;
        opt = -1;//not initialized
        String c = ";";
        endMessage = c.getBytes(StandardCharsets.UTF_8)[0];
        String o = "1";
        one = o.getBytes(StandardCharsets.UTF_8)[0];
    }

    // end of message ‘\0'
    @Override
    public Message decodeNextByte(byte nextByte) {
        if (opt == -1) {
            if (nextByte != endMessage) {
                pushByte(nextByte);
                if (len == 2) {
                    opt = bytesToShort(bytes);
                    popString();
                    return startDecodingMessage();
                }
            }
        } else {
            String str;
            switch (opt) {
                case 1: {
                    if (nextByte == '\0' | nextByte == endMessage) {
                        str = popString();
                        if (str.length() >= 2) {
                            if (zeroCount == 3) {
                                ((REGISTERMessage) message).setUsername(str);
                            } else if (zeroCount == 2) {
                                ((REGISTERMessage) message).setPassword(str);
                            } else if (zeroCount == 1) {
                                ((REGISTERMessage) message).setBirthday(str);
                                reset();
                                return message;
                            }
                            zeroCount--;
                        }
                    } else {
                        pushByte(nextByte);
                    }
                    break;
                }
                case 2: {
                    if (zeroCount == 0 & bytesExpected == 1) {
                        if (nextByte == one)
                            ((LOGINMessage) message).setCaptcha((byte) ('\1'));
                        else
                            ((LOGINMessage) message).setCaptcha((byte) ('\0'));
                        bytesExpected--;
                        reset();
                        return message;
                    } else {
                        if (nextByte == '\0') {
                            str = popString();
                            if (zeroCount == 2) {
                                ((LOGINMessage) message).setUsername(str);
                                zeroCount--;
                            } else if (zeroCount == 1) {
                                ((LOGINMessage) message).setPassword(str);
                                zeroCount--;
                            }
                        } else {
                            pushByte(nextByte);
                        }
                    }
                    break;
                }
                case 4: {
                    if (bytesExpected == 1) {
                        if (nextByte == one)
                            ((FOLLOWMessage) message).setFollow((byte) ('\1'));
                        else
                            ((FOLLOWMessage) message).setFollow((byte) ('\0'));
                        bytesExpected--;
                    } else if (bytesExpected == 0 && nextByte == '\0') {
                        str = popString();
                        ((FOLLOWMessage) message).setUsername(str);
                        zeroCount--;
                        reset();
                        return message;
                    } else {
                        pushByte(nextByte);
                    }
                    break;
                }
                case 5: {
                    if (nextByte == endMessage) {
                        str = popString();
                        ((POSTMessage) message).setContent(str);
                        ((POSTMessage) message).setUsersTags(str);
                        reset();
                        return message;
                    } else {
                        pushByte(nextByte);
                    }
                    break;
                }
                case 6: {
                    if (nextByte == endMessage) {
                        str = popString();
                        int index = str.indexOf(' ');
                        ((PMMessage) message).setContent(str.substring(index + 1));
                        ((PMMessage) message).setDateAndTime(getDate());
                        zeroCount--;
                        reset();
                        return message;
                    } else if (nextByte == '\0') {
                        str = popString();
                        ((PMMessage) message).setUsername(str);
                    } else {
                        pushByte(nextByte);
                    }
                    break;
                }
                case 8: {
                    if (nextByte == '\0') {
                        str = popString();
                        ((STATMessage) message).setUsersNames(str);
                        zeroCount--;
                        reset();
                        return message;
                    } else {
                        pushByte(nextByte);
                    }
                    break;
                }
                case 12: {
                    if (nextByte == endMessage) {
                        str = popString();
                        ((BLOCKMessage) message).setUserNameToBlock(str);
                        zeroCount--;
                        reset();
                        return message;
                    } else {
                        pushByte(nextByte);
                    }
                    break;
                }
            }
        }
        return null;
    }

    @Override
    public byte[] encode(Message message) {
        List<Byte> bytesArray = new ArrayList<Byte>();
        //add opt code bytes first
        short opt = message.getOpcode();
        byte[] optArray = shortToBytes(opt);
        bytesArray.add(optArray[0]);
        bytesArray.add(optArray[1]);
        switch (opt) {
            case 9: {
                NOTIFICATIONMessage notM = ((NOTIFICATIONMessage) message);
                bytesArray.add(notM.getNotificationType());
                pushBytes(bytesArray, notM.getPostingUserBytes());
                bytesArray.add((byte) '\0');
                pushBytes(bytesArray, notM.getContentBytes());
                bytesArray.add((byte) '\0');
                break;
            }
            case 10: {
                ACKMessage ackM = ((ACKMessage) message);
                short otherOp = ackM.getOtherOpcode();
                byte[] otherOpBytes = shortToBytes(otherOp);
                pushBytes(bytesArray, otherOpBytes);
                if (otherOp == 7 || otherOp == 8) {
                    List<short[]> list = ackM.getStats();
                    if (list.size() == 0)
                        break;
                    bytesArray.clear();
                    for (int i = 0; i < list.size(); i++) {//separator ;
                        pushBytes(bytesArray, optArray);
                        pushBytes(bytesArray, otherOpBytes);
                        short[] array = list.get(i);
                        for (short s : array) {
                            byte[] userBytes = shortToBytes(s);
                            pushBytes(bytesArray, userBytes);
                        }
                        bytesArray.add((byte) ';');
                    }
                    bytesArray.remove(bytesArray.size() - 1);
                } else if (otherOp == 4) {
                    pushBytes(bytesArray, ackM.getMsgBytes());
                }
                break;
            }
            case 11: {
                ERRORMessage errorM = ((ERRORMessage) message);
                byte[] userBytes = shortToBytes(errorM.getOtherOptCode());
                pushBytes(bytesArray, userBytes);
                break;
            }
        }
        byte[] output = new byte[bytesArray.size() + 1];
        for (int i = 0; i < bytesArray.size(); i++) {
            output[i] = bytesArray.get(i);
        }
        output[bytesArray.size()] = ';';
        reset();
        return output;
    }

    private String getDate() {
        LocalDateTime now = java.time.LocalDateTime.now();
        int year = now.getYear();
        String month = "";
        String day = "";
        String hh = "";
        String mm = "";
        if (now.getMonthValue() < 10)
            month = "0" + now.getMonthValue();
        else
            month = Integer.toString(now.getMonthValue());
        if (now.getDayOfMonth() < 10)
            day = "0" + now.getDayOfMonth();
        else
            day = Integer.toString(now.getDayOfMonth());
        if (now.getHour() < 10)
            hh = "0" + now.getHour();
        else
            hh = Integer.toString(now.getHour());
        if (now.getMinute() < 10)
            mm = "0" + now.getMinute();
        else
            mm = Integer.toString(now.getMinute());
        String time = day + "-" + month + "-" + year + " " + hh + ":" + mm; // check in the 9 am and in mm < 10
        return time;
    }

    private Message startDecodingMessage() {
        switch (opt) {
            case 1: {
                message = new REGISTERMessage();
                zeroCount = 3;
                break;
            }
            case 2: {
                message = new LOGINMessage();
                zeroCount = 2;
                bytesExpected = 1;
                break;
            }
            case 3: {
                message = new LOGOUTMessage();
                return message;
            }
            case 4: {
                message = new FOLLOWMessage();
                bytesExpected = 1;
                break;
            }
            case 5: {
                message = new POSTMessage();
                zeroCount = 1;
                break;
            }
            case 6: {
                message = new PMMessage();
                zeroCount = 3;
                break;
            }
            case 7: {
                message = new LOGSTATMessage();
                reset();
                return message;
            }
            case 8: {
                message = new STATMessage();
                zeroCount = 1;
                break;
            }
            case 12: {
                message = new BLOCKMessage();
                zeroCount = 2;
                break;
            }
        }
        return null;
    }

    public short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }

    private String popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

    private void pushBytes(List<Byte> array, byte[] toAdd) {
        for (int i = 0; i < toAdd.length; i++) {
            array.add(toAdd[i]);
        }
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private void reset() {
        bytes = new byte[1 << 10];
        zeroCount = -1;
        bytesExpected = 0;
        len = 0;
        opt = -1;//not initialized
        String c = ";";
        endMessage = c.getBytes(StandardCharsets.UTF_8)[0];
    }
}

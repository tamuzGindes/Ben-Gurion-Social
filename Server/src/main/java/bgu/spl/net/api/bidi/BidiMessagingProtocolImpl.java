package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Message;
import bgu.spl.net.api.Messages.*;
import bgu.spl.net.impl.rci.BGSData;
import bgu.spl.net.impl.rci.User;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {
    private boolean shouldTerminate;
    private int connectionId;
    private String username;
    private Connections<Message> connections;
    private BGSData data;
    //filters
    private String[] filteredWords = {"war", "trump", "noga", "cat"};

    public BidiMessagingProtocolImpl(BGSData data) {
        this.data = data;
        username = "";
        connectionId = -1;
        shouldTerminate = false;
        connections = null;
    }

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connectionId = connectionId;
        this.connections = connections;
    }

    @Override
    public void process(Message message) {
        System.out.print
                ("CLIENT#" + connectionId + "< ");
        int opt = message.getOpcode();
        User user;
        switch (opt) {
            case 1: {
                REGISTERMessage regM = ((REGISTERMessage) message);
                System.out.print
                        ("REGISTER " + regM.getUsername() + " " + regM.getPassword() + " " + regM.getBirthday());
                System.out.println();
                if (data.isConnected(regM.getUsername())) {
                    sendError(regM.getOpcode());
                } else {
                    user = new User(connectionId, regM.getUsername(), regM.getPassword(), regM.getBirthday());
                    data.addUser(user);
                    sendAck(regM.getOpcode(), new ACKMessage(regM.getOpcode()), "");
                }
                break;
            }
            case 2: {
                LOGINMessage loginM = ((LOGINMessage) message);
                System.out.print
                        ("LOGIN " + loginM.getUsername() + " " + loginM.getPassword() + " " + loginM.getCaptcha());
                System.out.println();
                if (data.isConnected(loginM.getUsername())) {
                    user = data.getUser(loginM.getUsername());
                    username = user.getUserName();
                    synchronized (user) {
                        if (!user.getLoginStatus().get() && user.getPassword().contentEquals(loginM.getPassword()) && loginM.getCaptcha() == '\1') {
                            user.login();
                            sendAck(loginM.getOpcode(), new ACKMessage(loginM.getOpcode()), "");
                            while (user.awaitingMessages()) {
                                connections.send(connectionId, user.readMessages());
                            }
                        } else {
                            sendError(loginM.getOpcode());
                        }
                    }
                } else {
                    sendError(loginM.getOpcode());
                }
                break;
            }
            case 3: {
                LOGOUTMessage logoutM = ((LOGOUTMessage) message);
                System.out.print
                        ("LOGOUT");
                System.out.println();
                if (data.isConnected(username)) {
                    user = data.getUser(username);
                    synchronized (user) {
                        if (user.getLoginStatus().get()) {
                            user.logout();
                            sendAck(logoutM.getOpcode(), new ACKMessage(logoutM.getOpcode()), "");
                            connections.disconnect(connectionId);
                            shouldTerminate = true;
                        } else {
                            sendError(logoutM.getOpcode());
                        }
                    }
                } else {
                    sendError(logoutM.getOpcode());
                }
                break;
            }
            case 4: {
                FOLLOWMessage followM = ((FOLLOWMessage) message);
                if (followM.getFollow() == '\1') {
                    System.out.print
                            ("FOLLOW 1 " + followM.getUsername());
                    System.out.println();
                } else {
                    System.out.print
                            ("FOLLOW 0 " + followM.getUsername());
                    System.out.println();
                }
                if (data.isConnected(username) & data.isConnected(followM.getUsername())) {
                    user = data.getUser(username);
                    User otherUser = data.getUser(followM.getUsername());
                    if (user.getLoginStatus().get()) {
                        if (followM.getFollow() == 0) {
                            if (!user.isFollowing(otherUser.getUserName()) && !otherUser.isBlocked(username) && !user.isBlocked(followM.getUsername())) {
                                user.follow(otherUser.getUserName(), otherUser);
                                otherUser.addFollow(username, user);
                                sendAck(followM.getOpcode(), new ACKMessage(followM.getOpcode(), otherUser.getUserName()), "");
                            } else {
                                sendError(followM.getOpcode());
                            }
                        } else {
                            if (user.isFollowing(otherUser.getUserName())) {
                                user.unFollow(otherUser.getUserName(), otherUser);
                                otherUser.removeFollower(username, user);
                                sendAck(followM.getOpcode(), new ACKMessage(followM.getOpcode(), otherUser.getUserName()), "");
                            } else {
                                sendError(followM.getOpcode());
                            }
                        }
                    } else {
                        sendError(followM.getOpcode());
                    }
                } else {
                    sendError(followM.getOpcode());
                }
                break;
            }
            case 5: {
                POSTMessage postM = ((POSTMessage) message);
                System.out.print
                        ("POST " + postM.getContent());
                System.out.println();
                if (data.isConnected(username)) {
                    user = data.getUser(username);
                    if (user.getLoginStatus().get()) {
                        user.post(postM.getContent());
                        Set<User> recipientUsers = new HashSet<>();
                        List<String> tagged = postM.getUsersTags();
                        for (String userNameTagged : tagged) {
                            if (data.isConnected(userNameTagged)) {
                                User recipientUser = data.getUser(userNameTagged);
                                if (recipientUser.isFollowedBy(username) && !recipientUser.isBlocked(username) && !user.isBlocked(userNameTagged))
                                    recipientUsers.add(recipientUser);
                            }
                        }
                        ConcurrentHashMap<String, User> followers = user.getFollowers();
                        for (Map.Entry<String, User> follower : followers.entrySet()) {
                            if (data.isConnected(follower.getKey()) && !follower.getValue().isBlocked(username)) {
                                recipientUsers.add(follower.getValue());
                            }
                        }
                        NOTIFICATIONMessage notificationM = new NOTIFICATIONMessage((byte) 1, username, postM.getContent());

                        for (User userToSend : recipientUsers) {
                            sendNotification(userToSend, notificationM);
                        }
                        sendAck(postM.getOpcode(), new ACKMessage(postM.getOpcode()), "");
                    } else {
                        sendError(postM.getOpcode());
                    }
                } else {
                    sendError(postM.getOpcode());
                }
                break;
            }
            case 6: {
                PMMessage pmM = ((PMMessage) message);
                System.out.print
                        ("PM " + pmM.getUsername() + " " + pmM.getContent());
                System.out.println();
                if (data.isConnected(username) && data.isConnected(pmM.getUsername())) {
                    user = data.getUser(username);
                    User recipientUser = data.getUser(pmM.getUsername());
                    if (user.getLoginStatus().get() && !recipientUser.isBlocked(username) && !user.isBlocked(pmM.getUsername()) && user.isFollowing(pmM.getUsername())) {
                        String filteredContent = filterPM(pmM.getContent());
                        user.sendPM(filteredContent);
                        NOTIFICATIONMessage notificationM = new NOTIFICATIONMessage((byte) 0, username, filteredContent + " " + pmM.getDateAndTime());
                        sendNotification(recipientUser, notificationM);
                        sendAck(pmM.getOpcode(), new ACKMessage(pmM.getOpcode()), "");
                    } else {
                        sendError(pmM.getOpcode());
                    }
                } else {
                    sendError(pmM.getOpcode());
                }
                break;
            }
            case 7: {
                LOGSTATMessage logStatM = ((LOGSTATMessage) message);
                System.out.print
                        ("LOGSTAT");
                System.out.println();
                if (data.isConnected(username)) {
                    user = data.getUser(username);
                    if (user.getLoginStatus().get()) {
                        List<short[]> logs = new ArrayList<>();
                        Collection<User> users = data.getUsers().values();
                        for (User userToLog : users) {
                            if (userToLog.getLoginStatus().get() && !userToLog.isBlocked(username) && !user.isBlocked(userToLog.getUserName())) {
                                short[] log = {(short) userToLog.getAge(), (short) userToLog.getNumOfPosts().get(), (short) userToLog.getNumOfFollowers().get(), (short) userToLog.getNumOfFollowing().get()};
                                logs.add(log);
                            }
                        }
                        sendAck(logStatM.getOpcode(), new ACKMessage(logStatM.getOpcode(), logs), "");
                        logs.clear();
                    } else {
                        sendError(logStatM.getOpcode());
                    }
                } else {
                    sendError(logStatM.getOpcode());
                }
                break;
            }
            case 8: {
                STATMessage statM = ((STATMessage) message);
                List<String> usernames = statM.getUsersNames();
                String replace = (usernames.toString()).replace('[', ' ').replace(']', ' ');
                System.out.print
                        ("STAT " + replace);
                System.out.println();
                if (data.isConnected(username)) {
                    user = data.getUser(username);
                    if (user.getLoginStatus().get()) {
                        List<short[]> stats = new ArrayList<>();
                        String users = "";
                        for (String userNameToStat : usernames) {
                            if (data.isConnected(userNameToStat)) {
                                User userToStat = data.getUser(userNameToStat);
                                if (!userToStat.isBlocked(username) && !user.isBlocked(userNameToStat)) {
                                    users = users + " " + userNameToStat;
                                    short[] stat = {(short) userToStat.getAge(), (short) userToStat.getNumOfPosts().get(), (short) userToStat.getNumOfFollowers().get(), (short) userToStat.getNumOfFollowing().get()};
                                    stats.add(stat);
                                } else {
                                    sendError(statM.getOpcode());
                                    return;
                                }
                            } else {
                                sendError(statM.getOpcode());
                                return;
                            }
                        }
                        sendAck(statM.getOpcode(), new ACKMessage(statM.getOpcode(), stats), replace);
                        stats.clear();
                    } else {
                        sendError(statM.getOpcode());
                    }
                } else {
                    sendError(statM.getOpcode());
                }
                break;
            }
            case 12: {
                BLOCKMessage blockM = ((BLOCKMessage) message);
                String blockedName = blockM.getUserNameToBlock();
                System.out.print
                        ("BLOCK " + blockedName);
                System.out.println();
                if (data.isConnected(username) && data.isConnected(blockM.getUserNameToBlock())) {
                    user = data.getUser(username);
                    if (user.getLoginStatus().get()) {
                        User blocked = data.getUser(blockedName);
                        user.block(blockedName, blocked);
                        if (user.isFollowedBy(blockedName)) {
                            user.removeFollower(blockedName, blocked);
                            blocked.unFollow(username, user);
                        }
                        if (user.isFollowing(blockedName)) {
                            user.unFollow(blockedName, blocked);
                            blocked.removeFollower(username, user);
                        }
                        sendAck(blockM.getOpcode(), new ACKMessage(blockM.getOpcode(), blockedName), blockedName);
                    } else {
                        sendError(blockM.getOpcode());
                    }
                } else {
                    sendError(blockM.getOpcode());
                }
                break;
            }
        }
    }

    public String filterPM(String content) {
        for (int i = 0; i < filteredWords.length; i++) {
            content = content.replaceAll(filteredWords[i], "<filter>");
        }
        return content;

    }

    public void sendNotification(User user, NOTIFICATIONMessage notifM) {
        System.out.print
                ("CLIENT#" + user.getConnectionId() + "> NOTIFICATION");
        if (notifM.getNotificationType() == '\1')
            System.out.print(" Public");
        else
            System.out.print(" PM");
        System.out.print
                (" " + notifM.getPostingUser() + " " + notifM.getContent());
        System.out.println();
        if (user.getLoginStatus().get()) {
            connections.send(user.getConnectionId(), notifM);
        } else {
            user.addMessages(notifM);
        }
    }

    public void sendError(short otherOp) {
        System.out.print
                ("CLIENT#" + connectionId + "> ERROR " + otherOp);
        System.out.println();
        connections.send(connectionId, new ERRORMessage(otherOp));
    }

    public void sendAck(short otherOp, ACKMessage ackMessage, String print) {
        System.out.print
                ("CLIENT#" + connectionId + "> ACK " + otherOp + " " + print);
        System.out.println();
        connections.send(connectionId, ackMessage);
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}

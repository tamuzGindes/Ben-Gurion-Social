package bgu.spl.net.impl.rci;

import bgu.spl.net.api.Message;
import bgu.spl.net.api.Messages.NOTIFICATIONMessage;

import java.text.spi.DateFormatProvider;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private int id;
    private int connectionId;
    private String userName;
    private String Password;
    private String Birthday;
    AtomicInteger numOfFollowers;
    AtomicInteger numOfFollowing;
    private AtomicBoolean loginStatus;
    private AtomicInteger numOfPosts;
    private ConcurrentHashMap<String, User> followers;
    private ConcurrentHashMap<String, User> following;
    private ConcurrentHashMap<String, User> blocked;
    private ConcurrentLinkedQueue<NOTIFICATIONMessage> messages;//messages queue of messages sent to the user when he wasn't login
    private ConcurrentLinkedDeque<String> posts;
    private ConcurrentLinkedDeque<String> pms;

    public User(int connectionId, String userName, String password, String birthday) {
        this.id = -1;
        this.connectionId = connectionId;
        this.userName = userName;
        this.Password = password;
        this.Birthday = birthday;
        numOfFollowers = new AtomicInteger(0);
        numOfFollowing = new AtomicInteger(0);
        followers = new ConcurrentHashMap<>();
        following = new ConcurrentHashMap<>();
        posts = new ConcurrentLinkedDeque<String>();
        pms = new ConcurrentLinkedDeque<String>();
        messages = new ConcurrentLinkedQueue<NOTIFICATIONMessage>();
        loginStatus = new AtomicBoolean(false);
        numOfPosts = new AtomicInteger(0);
        blocked = new ConcurrentHashMap<>();

    }

    public String getUserName() {
        return userName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return Password;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public AtomicInteger getNumOfFollowers() {
        return numOfFollowers;
    }

    public AtomicInteger getNumOfFollowing() {
        return numOfFollowing;
    }

    public AtomicBoolean getLoginStatus() {
        return loginStatus;
    }

    public AtomicInteger getNumOfPosts() {
        return numOfPosts;
    }

    public ConcurrentHashMap<String, User> getFollowers() {
        return followers;
    }

    public ConcurrentHashMap<String, User> getFollowing() {
        return following;
    }

    public ConcurrentLinkedDeque<String> getPosts() {
        return posts;
    }

    public ConcurrentLinkedDeque<String> getPms() {
        return pms;
    }

    public int getId() {
        return id;
    }

    public String getBirthday() {
        return Birthday;
    }

    public int getAge() {
        String birthdayYear = Birthday.substring(6);
        int yearBirthday = Integer.parseInt(birthdayYear);
        LocalDate now = java.time.LocalDate.now();
        int yearNow = now.getYear();
        int age = yearNow - yearBirthday;
        return age;
    }

    public boolean isBlocked(String userName) {
        return blocked.containsKey(userName);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void login() {
        loginStatus.set(true);
    }

    public void logout() {
        loginStatus.set(false);
    }

    public boolean isFollowedBy(String userName) {
        return followers.containsKey(userName);
    }

    public boolean isFollowing(String userName) {
        return following.containsKey(userName);
    }

    public void follow(String userName, User toFollow) {
        following.put(userName, toFollow);
        numOfFollowing.incrementAndGet();

    }

    public void addFollow(String userName, User follower) {
        followers.put(userName, follower);
        numOfFollowers.incrementAndGet();

    }

    public void unFollow(String userName, User toUnfollow) {
        following.remove(userName, toUnfollow);
        numOfFollowing.decrementAndGet();
    }

    public void removeFollower(String userName, User follower) {
        followers.remove(userName, follower);
        numOfFollowers.decrementAndGet();

    }

    public void post(String content) {
        posts.add(content);
        numOfPosts.incrementAndGet();
    }

    public void sendPM(String content) {
        pms.add(content);
    }

    public void block(String userName, User userBlocked) {
        blocked.put(userName, userBlocked);
    }

    public boolean awaitingMessages() {
        return !messages.isEmpty();
    }

    public void addMessages(NOTIFICATIONMessage message) {
        messages.add(message);
    }

    public NOTIFICATIONMessage readMessages() {
        if (!messages.isEmpty())
            return messages.poll();
        return null;
    }


}

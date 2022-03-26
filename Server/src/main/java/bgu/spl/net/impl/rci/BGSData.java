package bgu.spl.net.impl.rci;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BGSData {
    private ConcurrentHashMap<String,User> usersMap;
    AtomicInteger usersCount;
    public BGSData() {

        usersMap = new ConcurrentHashMap<>();
        usersCount = new AtomicInteger(1);
    }

    public void addUser(User user){
        user.setId(usersCount.get());
        usersCount.incrementAndGet();
        usersMap.put(user.getUserName() ,user);
    }

    public User getUser(String userName){
        return usersMap.get(userName);
    }

    public ConcurrentHashMap<String, User> getUsers() {
        return usersMap;
    }
    public boolean isConnected(String userName) {
        return usersMap.containsKey(userName);
    }
}

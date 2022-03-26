package bgu.spl.net.srv;
import bgu.spl.net.api.Messages.REGISTERMessage;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImp<T> implements Connections<T> {
    ConcurrentHashMap<Integer, ConnectionHandler<T>> connections;
    AtomicInteger connectionCounter;

    public ConnectionsImp() {
        connections = new ConcurrentHashMap<Integer, ConnectionHandler<T>>();
        connectionCounter = new AtomicInteger(1);
    }

    @Override
    public synchronized boolean send(int connectionId, T msg) {
        if (connections.containsKey(connectionId)) {
            connections.get(connectionId).send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void broadcast(T msg) {
        for (ConnectionHandler<T> handler : connections.values()) {
                handler.send(msg);
        }

    }

    @Override
    public synchronized void disconnect(int connectionId) {
        if (connections.containsKey(connectionId)) {
            connections.remove(connectionId);
        }
    }

    public synchronized int connect(ConnectionHandler<T> connection, BidiMessagingProtocol<T> protocol) {
        int idC = connectionCounter.get();
        connections.put(idC, connection);
        protocol.start(idC,this);
        connectionCounter.incrementAndGet();
        return idC;
    }
}
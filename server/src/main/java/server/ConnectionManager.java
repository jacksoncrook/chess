package server;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Collection<Session>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, Session session) {
        Collection<Session> sessions = connections.computeIfAbsent(gameID, k -> new ArrayList<>());
        sessions.add(session);
        connections.replace(gameID, sessions);
    }

    public void remove(Integer gameID, Session session) {
        Collection<Session> sessions = connections.get(gameID);
        sessions.remove(session);
        connections.replace(gameID, sessions);
        session.close();
    }

    public void broadcast(Integer gameID, Session excludeSession, ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        for (Session c : connections.computeIfAbsent(gameID, k -> new ArrayList<>())) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }

    public void sendMsg(Session session, ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        session.getRemote().sendString(msg);
    }
}
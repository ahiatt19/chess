package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(int gameID, String authToken, Session session) {
        var connection = new Connection(gameID, authToken, session);
        connections.put(authToken, connection);
        System.out.println("Connections: " + connections);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(String excludeauthToken, ServerMessage notification) throws Exception {
        var removeList = new ArrayList<Connection>();
        System.out.println("in broadcast");
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                // c.send(notification.getMessage());
                if (!c.authToken.equals(excludeauthToken)) {
                    c.send(notification.getMessage());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }
}

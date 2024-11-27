package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

public class Connection {
    public int gameID;
    public Session session;
    public String authToken;

    public Connection(int gameID, String authToken, Session session) {
        this.gameID = gameID;
        this.session = session;
        this.authToken = authToken;
    }

    public void send(String msg) throws Exception {
        session.getRemote().sendString(msg);
    }

    @Override
    public String toString() {
        return "{" + "ID: '" + gameID + ", session: " + session + '}';
    }
}

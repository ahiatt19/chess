package server.websocket;

import chess.ChessGame;
import com.google.gson.*;
import org.eclipse.jetty.websocket.api.Session;
import server.GetGameHandler;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotficationMessage;
import websocket.messages.ServerMessage;

import java.lang.reflect.Type;
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

    public void broadcast(String excludeauthToken, NotficationMessage notification) throws Exception {
        var removeList = new ArrayList<Connection>();
        System.out.println("in broadcast");
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludeauthToken)) {
                    Gson gson = new Gson();
                    var json = gson.toJson(notification);
                    System.out.println(json);
                    c.send(json);
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

    public void send(String authToken, LoadGameMessage loadGameMessage) throws Exception {
        var removeList = new ArrayList<Connection>();
        System.out.println("in send");
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.authToken.equals(authToken)) {
                    var gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(ChessGame.class, new ChessGameSerializer());
                    var serializer = gsonBuilder.create();

                    var loadGameJSON = serializer.toJson(loadGameMessage);
                    System.out.println(loadGameJSON);
                    c.send(loadGameJSON);
                }
            } else {
                removeList.add(c);
            }
        }
    }

    public class ChessGameSerializer implements JsonSerializer<ChessGame> {
        @Override
        public JsonElement serialize(ChessGame chessGame, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();

            // Serialize the current team turn
            jsonObject.addProperty("currentTeamTurn", chessGame.getTeamTurn().toString());

            // Serialize the chess board (game)
            JsonElement boardJson = context.serialize(chessGame.getBoard());
            jsonObject.add("game", boardJson);

            return jsonObject;
        }
    }
}

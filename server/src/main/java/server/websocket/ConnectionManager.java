package server.websocket;

import chess.ChessGame;
import com.google.gson.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(int gameID, String authToken, Session session) {
        var connection = new Connection(gameID, authToken, session);
        connections.put(authToken, connection);
        // System.out.println("Connections: " + connections);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(String excludeauthToken, ServerMessage message, int gameID) throws Exception {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludeauthToken) && c.gameID == gameID) {
                    if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                        var gsonBuilder = new GsonBuilder();
                        gsonBuilder.registerTypeAdapter(ChessGame.class, new ChessGameSerializer());
                        var serializer = gsonBuilder.create();

                        var loadGameJSON = serializer.toJson(message);
                        System.out.println(loadGameJSON);
                        c.send(loadGameJSON);
                    } else {
                        Gson gson = new Gson();
                        var json = gson.toJson(message);
                        System.out.println(json);
                        c.send(json);
                    }
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

    public void send(String authToken, ServerMessage serverMessage) throws Exception {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.authToken.equals(authToken)) {
                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                        var gsonBuilder = new GsonBuilder();
                        gsonBuilder.registerTypeAdapter(ChessGame.class, new ChessGameSerializer());
                        var serializer = gsonBuilder.create();

                        var loadGameJSON = serializer.toJson(serverMessage);
                        System.out.println(loadGameJSON);
                        c.send(loadGameJSON);
                    }

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
            jsonObject.addProperty("gameOver", chessGame.getGameOver());

            // Serialize the chess board (game)
            JsonElement boardJson = context.serialize(chessGame.getBoard());
            jsonObject.add("game", boardJson);

            return jsonObject;
        }
    }
}

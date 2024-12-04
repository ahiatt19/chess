package server;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.*;
import handler.obj.*;
import model.*;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public AuthData register(UserData userData) throws Exception {
        var path = "/user";
        return this.makeRequest("POST", path, userData, AuthData.class, null);
    }

    public AuthData login(LoginRequest loginRequest) throws Exception {
        var path = "/session";
        return this.makeRequest("POST", path, loginRequest, AuthData.class, null);
    }

    public void logout(String authToken) throws Exception {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, authToken);
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        var path = "/game";
        return this.makeRequest("GET", path, null, ListGamesResult.class, authToken);
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest createGame) throws Exception {
        var path = "/game";
        return this.makeRequest("POST", path, createGame, CreateGameResult.class, authToken);
    }

    public void joinGame(String authToken, JoinGameRequest joinGame) throws Exception {
        var path = "/game";
        this.makeRequest("PUT", path, joinGame, null, authToken);
    }

    public void clear() throws Exception {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    public GameData getGame(String authToken, int gameID) throws Exception {
        var path = "/chess?gameID=" + gameID;
        return this.makeRequest("GET", path, null, GameData.class, authToken);
    }

    public void updateGame(String authToken, int gameID, ChessGame game) throws Exception {
        var path = "/update?gameID=" + gameID;
        this.makeRequest("PUT", path, game, null, authToken);
    }




    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String header) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http, header);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private void writeBody(Object request, HttpURLConnection http, String header) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            if (header != null) {
                http.addRequestProperty("Authorization", header);
            }
            String reqData = new Gson().toJson(request);
            if (request.getClass() == ChessGame.class) {
                var gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(ChessGame.class, new ChessGameSerializer());
                var serializer = gsonBuilder.create();

                reqData = serializer.toJson(request);

                // reqData = new Gson().toJson(request, UpdateGameRequest.class);
            }
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
        else if (header != null) {
            http.addRequestProperty("Authorization", header);
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws Exception {
        var status = http.getResponseCode();
        if (status != 200) {
            if (status == 400) {
                throw new Exception("Something was typed wrong, try again");
            }
            else if (status == 401) {
                throw new Exception("You are not authorized");
            }
            else if (status == 403) {
                throw new Exception("Already taken");
            }
            else if (status == 500) {
                throw new Exception("Error, try again");
            }
            throw new Exception("failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    if (responseClass == GameData.class) {
                        Gson serializer = createSerializer();

                        response = serializer.fromJson(reader, responseClass);
                    } else {
                        response = new Gson().fromJson(reader, responseClass);
                    }
                }
            }
        }
        return response;
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

    public static Gson createSerializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Register the ChessGame deserializer
        gsonBuilder.registerTypeAdapter(ChessGame.class, new JsonDeserializer<ChessGame>() {
            @Override
            public ChessGame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();

                // Deserialize ChessBoard from the JSON
                ChessBoard chessBoard = context.deserialize(jsonObject.get("game"), ChessBoard.class);
                ChessGame game = new ChessGame();
                game.setBoard(chessBoard);

                if (jsonObject.has("currentTeamTurn")) {
                    String currentTurn = jsonObject.get("currentTeamTurn").getAsString();
                    game.setTeamTurn(ChessGame.TeamColor.valueOf(currentTurn));
                }
                if (jsonObject.has("gameOver")) {
                    boolean gameOver = jsonObject.get("gameOver").getAsBoolean();
                    game.setGameOver(gameOver);
                }

                return game;
            }
        });
        return gsonBuilder.create();
    }
}

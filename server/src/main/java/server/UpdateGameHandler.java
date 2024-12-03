package server;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.*;
import dataaccess.DataAccessException;
import handler.obj.UpdateGameRequest;
import service.Service;
import spark.Request;
import spark.Response;

import java.lang.reflect.Type;
import java.util.Objects;

public class UpdateGameHandler {
    private final Service service;

    public UpdateGameHandler(Service service) {
        this.service = service;
    }

    public Object handleRequest (Request req, Response res) {
        Gson gson = new Gson();
        try {
            int gameID = Integer.parseInt(req.queryParams("gameID"));
            //My service updateGame function returns strings that I compare for the response
            Gson serializer = createSerializer();

            ChessGame chessGame = serializer.fromJson(req.body(), ChessGame.class);
            String result = service.updateAGame(req.headers("Authorization"), gameID, chessGame);
            //bad request
            if (Objects.equals(result, "400")) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }
            //not correct auth token
            if (Objects.equals(result, "401")) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
            //someone is already in that color and game
            if (Objects.equals(result, "403")) {
                res.status(403);
                return gson.toJson(new ErrorResponse("Error: already taken"));
            }
            return gson.toJson(new Object());
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
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

                return game;
            }
        });
        return gsonBuilder.create();
    }
}

package server;

import chess.ChessGame;
import com.google.gson.*;
import dataaccess.DataAccessException;
import dataaccess.MySQLGameDAO;
import model.GameData;
import service.Service;
import spark.Request;
import spark.Response;

import java.lang.reflect.Type;

public class GetGameHandler {
    private final Service service;

    public GetGameHandler(Service service) {
        this.service = service;
    }

    public Object handleRequest (Request req, Response res) {
        Gson gson = new Gson();
        try {
            int gameID = Integer.parseInt(req.queryParams("gameID"));
            GameData gameData = service.getGame(req.headers("Authorization"), gameID);

            var gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(ChessGame.class, new ChessGameSerializer());
            var serializer = gsonBuilder.create();

            var chessGameString = serializer.toJson(gameData);
            //bad auth token
            if (gameData == null) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
            return chessGameString;
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
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

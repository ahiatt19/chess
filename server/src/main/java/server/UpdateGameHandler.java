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

import static jsonSerializers.Serializers.createSerializer;

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
            //not correct auth token
            if (Objects.equals(result, "401")) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
            return gson.toJson(new Object());
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

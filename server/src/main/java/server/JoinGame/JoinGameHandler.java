package server.JoinGame;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import server.ErrorResponse;
import server.ListGames.ListGamesResult;
import server.Register.RegisterRequest;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Objects;

public class JoinGameHandler {
    private final UserService service;

    public JoinGameHandler(UserService service) {
        this.service = service;
    }

    public Object handleRequest (Request req, Response res) throws DataAccessException {
        Gson gson = new Gson();
        try {
            JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);
            String result = service.joinGame(req.headers("Authorization"), request);
            if (Objects.equals(result, "400")) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }
            if (Objects.equals(result, "401")) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
            if (Objects.equals(result, "403")) {
                res.status(403);
                return gson.toJson(new ErrorResponse("Error: already taken"));
            }
            return gson.toJson(result);
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

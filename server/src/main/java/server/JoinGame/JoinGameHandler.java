package server.JoinGame;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import server.ErrorResponse;
import service.Service;
import spark.Request;
import spark.Response;

import java.util.Objects;

public class JoinGameHandler {
    private final Service service;

    public JoinGameHandler(Service service) {
        this.service = service;
    }

    public Object handleRequest (Request req, Response res) {
        Gson gson = new Gson();
        try {
            JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);
            String result = service.updateGame(req.headers("Authorization"), request);
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
            return gson.toJson(new Object());
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

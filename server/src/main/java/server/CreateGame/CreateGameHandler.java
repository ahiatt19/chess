package server.CreateGame;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import server.ErrorResponse;
import service.Service;
import spark.Request;
import spark.Response;

import java.util.Objects;

public class CreateGameHandler {
    private final Service service;

    public CreateGameHandler(Service service) {
        this.service = service;
    }

    public Object handleRequest (Request req, Response res) {
        Gson gson = new Gson();
        try {
            CreateGameRequest request = gson.fromJson(req.body(), CreateGameRequest.class);
            if (Objects.equals(request.getGameName(), "")) {
                res.status(400); // Bad Request
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }
            CreateGameResult result = service.createGame(request.getGameName(), req.headers("Authorization"));
            if (result == null) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
            return gson.toJson(result);
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

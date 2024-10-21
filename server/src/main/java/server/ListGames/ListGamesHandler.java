package server.ListGames;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import server.CreateGame.CreateGameRequest;
import server.CreateGame.CreateGameResult;
import server.ErrorResponse;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Objects;

public class ListGamesHandler {
    private final UserService service;

    public ListGamesHandler(UserService service) {
        this.service = service;
    }

    public Object handleRequest (Request req, Response res) throws DataAccessException {
        Gson gson = new Gson();
        try {
            ListGamesResult result = service.listGames(req.headers("Authorization"));
            System.out.println(result);
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

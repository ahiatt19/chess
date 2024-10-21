package server.Logout;

import dataaccess.DataAccessException;
import server.ListGames.ListGamesResult;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import service.UserService;
import server.ErrorResponse;

import java.util.Objects;


public class LogoutHandler {
    private final UserService service;

    public LogoutHandler(UserService service) {
        this.service = service;
    }

    public Object handleRequest (Request req, Response res) throws DataAccessException {
        Gson gson = new Gson();
        try {
            ListGamesResult result = service.listGames(req.headers("Authorization"));
            if (Objects.equals(result, "401")) {
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

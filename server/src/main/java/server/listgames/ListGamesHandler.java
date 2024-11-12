package server.listgames;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.ListGamesResult;
import server.ErrorResponse;
import service.Service;
import spark.Request;
import spark.Response;


public class ListGamesHandler {
    private final Service service;

    public ListGamesHandler(Service service) {
        this.service = service;
    }

    public Object handleRequest (Request req, Response res) {
        Gson gson = new Gson();
        try {
            ListGamesResult result = service.listGames(req.headers("Authorization"));
            //bad auth token
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

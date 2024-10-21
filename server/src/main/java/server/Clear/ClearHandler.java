package server.Clear;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import server.ErrorResponse;
import server.JoinGame.JoinGameRequest;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Objects;

public class ClearHandler {
    private final UserService service;

    public ClearHandler(UserService service) {
        this.service = service;
    }

    public Object handleRequest (Request req, Response res) throws DataAccessException {
        Gson gson = new Gson();
        try {
            service.clear();
            return gson.toJson(new Object());
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

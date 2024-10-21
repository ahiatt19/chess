package server.Clear;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import server.ErrorResponse;
import service.Service;
import spark.Request;
import spark.Response;


public class ClearHandler {
    private final Service service;

    public ClearHandler(Service service) {
        this.service = service;
    }

    public Object handleRequest (Request req, Response res) {
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

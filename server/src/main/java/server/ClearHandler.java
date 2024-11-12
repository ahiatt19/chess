package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.Service;
import spark.Response;


public class ClearHandler {
    private final Service service;

    public ClearHandler(Service service) {
        this.service = service;
    }

    public Object handleRequest (Response res) {
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

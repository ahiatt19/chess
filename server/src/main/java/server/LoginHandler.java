package server;

import dataaccess.DataAccessException;
import handler.obj.LoginRequest;
import handler.obj.LoginResult;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import service.Service;


public class LoginHandler {
    private final Service service;

    public LoginHandler(Service service) {
        this.service = service;
    }

    public Object handleRequest (Request req, Response res) {
        Gson gson = new Gson();
        try {
            LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);

            LoginResult result = service.login(request);

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

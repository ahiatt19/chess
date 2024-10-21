package server.Register;

import dataaccess.DataAccessException;
import server.ErrorResponse;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import service.Service;


public class RegisterHandler {
    private final Service service;

    public RegisterHandler(Service service) {
        this.service = service;
    }

    public Object handleRequest (Request req, Response res) {
        Gson gson = new Gson();
        try {
            RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
            //Username or password are null
            if (request.getUsername() == null || request.getPassword() == null) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }
            RegisterResult result = service.register(request);
            //username is already taken
            if (result == null) {
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

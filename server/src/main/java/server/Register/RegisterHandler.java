package server.Register;

import dataaccess.DataAccessException;
import server.ErrorResponse;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import service.UserService;

import java.util.Objects;

public class RegisterHandler {
    private final UserService service;

    public RegisterHandler(UserService service) {
        this.service = service;
    }

    public Object handleRequest (Request req, Response res) throws DataAccessException {
        Gson gson = new Gson();
        try {
            RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
            if (request.getUsername() == null || request.getPassword() == null) {
                res.status(400); // Bad Request
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }
            RegisterResult result = service.register(request);
            System.out.println(result);
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

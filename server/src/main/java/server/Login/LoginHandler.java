package server.Login;

import dataaccess.DataAccessException;
import server.ErrorResponse;
import server.Register.RegisterRequest;
import server.Register.RegisterResult;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import service.UserService;

import java.util.Objects;

public class LoginHandler {
    private final UserService service;

    public LoginHandler(UserService service) {
        this.service = service;
    }

    public Object handleRequest (Request req, Response res) throws DataAccessException {
        Gson gson = new Gson();
        try {
            LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);

            LoginResult result = service.login(request);
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

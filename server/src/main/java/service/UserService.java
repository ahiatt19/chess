package service;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import server.RegisterResult;
import server.RegisterRequest;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;

import java.util.UUID;

public class UserService {

    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;

    public UserService(UserDAO userDataAccess, AuthDAO authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        try {
            UserData userData = new UserData(request.getUsername(), request.getPassword(), request.getEmail());

            UserData user = userDataAccess.getUser(request.getUsername());
            if (user == null) {
                System.out.println("User is null: " + request.getUsername());
                userDataAccess.createUser(userData);
                AuthData auth = new AuthData(request.getUsername(), UUID.randomUUID().toString());
                authDataAccess.createAuth(auth);
                return new RegisterResult(auth);
            }
            else {
                System.out.println("USERNAME ALREADY EXISTS");
                return null;
            }
        } catch (DataAccessException e){
            throw new DataAccessException("Failed to register user");
        }


    }
}

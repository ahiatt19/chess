package dataaccess;

import model.AuthData;


public interface AuthDAO {
    void createAuth(AuthData a);

    AuthData getAuth(String authToken);

    void deleteAuth(String authToken);

    void clearAuths();
}



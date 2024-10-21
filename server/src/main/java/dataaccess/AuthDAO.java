package dataaccess;

import model.AuthData;


public interface AuthDAO {
    void createAuth(AuthData a);

    AuthData getAuth(String username);

    void deleteAuth(String username);
}



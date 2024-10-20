package dataaccess;

import model.AuthData;


public interface AuthDAO {
    void createAuth(AuthData a) throws DataAccessException;

    AuthData getAuth(String username) throws DataAccessException;

    void deleteAuth(String username) throws DataAccessException;
}



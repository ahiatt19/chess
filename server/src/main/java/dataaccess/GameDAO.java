package dataaccess;

import model.GameData;

public interface GameDAO {
    GameData createGame(String gameName) throws DataAccessException;

    //UserData getUser(String username) throws DataAccessException;
}



package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    GameData createGame(String gameName) throws DataAccessException ;

    ArrayList<GameData> listGames() throws DataAccessException ;

    GameData getGame(int gameID) throws DataAccessException ;

    void updateGame(GameData gameData) throws DataAccessException ;

    void clearGames() throws DataAccessException ;
}



package dataaccess;

import model.GameData;
import model.ListGamesData;

import java.util.ArrayList;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException ;

    ArrayList<ListGamesData> listGames() throws DataAccessException ;

    GameData getGame(int gameID) throws DataAccessException ;

    void updateGame(GameData gameData) throws DataAccessException ;

    void clearGames() throws DataAccessException ;
}



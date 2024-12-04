package dataaccess;

import chess.ChessGame;
import model.GameData;
import handler.obj.ListGamesData;

import java.util.ArrayList;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException ;

    ArrayList<ListGamesData> listGames() throws DataAccessException ;

    GameData getGame(int gameID) throws DataAccessException ;

    void joinGame(GameData gameData) throws DataAccessException ;

    void updateGame(int gameID, ChessGame game) throws DataAccessException;

    void clearGames() throws DataAccessException ;
}



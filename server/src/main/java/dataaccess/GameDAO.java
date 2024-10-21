package dataaccess;

import model.GameData;
import server.JoinGame.JoinGameRequest;
import server.ListGames.ListGamesResult;

import java.util.ArrayList;

public interface GameDAO {
    GameData createGame(String gameName);

    ArrayList<GameData> listGames();

    GameData getGame(int gameID);

    void updateGame(GameData gameData);
}



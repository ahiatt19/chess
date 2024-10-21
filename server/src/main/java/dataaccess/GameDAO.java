package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    GameData createGame(String gameName);

    ArrayList<GameData> listGames();

    GameData getGame(int gameID);

    void updateGame(GameData gameData);

    void clearGames();
}



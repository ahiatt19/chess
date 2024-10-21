package server.ListGames;

import model.GameData;
import java.util.ArrayList;

public class ListGamesResult {
    ArrayList<GameData> gameDataList = new ArrayList<>();

    public ListGamesResult (ArrayList<GameData> gameDataList) {
        this.gameDataList = gameDataList;
    }
}

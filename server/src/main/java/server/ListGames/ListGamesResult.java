package server.ListGames;

import model.GameData;
import java.util.ArrayList;

public class ListGamesResult {
    ArrayList<GameData> games;

    public ListGamesResult (ArrayList<GameData> games) {
        this.games = games;
    }


}

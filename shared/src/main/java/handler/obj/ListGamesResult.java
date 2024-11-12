package handler.obj;

import java.util.ArrayList;

public class ListGamesResult {
    ArrayList<ListGamesData> games;

    public ListGamesResult (ArrayList<ListGamesData> games) {
        this.games = games;
    }

    public ArrayList<ListGamesData> getGames() {
        return games;
    }


}

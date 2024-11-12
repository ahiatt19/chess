package model;

import java.util.ArrayList;

public class CreateGameRequest {
    String gameName;

    public CreateGameRequest (String gameName) {
        this.gameName = gameName;
    }
    public String getGameName() {
        return this.gameName;
    }
}
package ui;

public class GamePlayUI {
    private int gameID;
    private String userGameID;
    private String playerColor;
    private UserType type;

    public void setVars(int originalID, String userID, String color, UserType userType) {
        this.gameID = originalID;
        this.userGameID = userID;
        this.playerColor = color;
        this.type = userType;
    }

    public int getGameID() {
        return this.gameID;
    }

    public String getUserGameID() {
        return userGameID;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public UserType getUserType() {
        return type;
    }
}


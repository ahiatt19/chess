package server.joingame;

public class JoinGameRequest {
    String playerColor;
    int gameID;

    public JoinGameRequest(String playerColor, int gameID) {
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public String getPlayerColor() {
        return this.playerColor;
    }

    public int getGameID() {
        return this.gameID;
    }
}

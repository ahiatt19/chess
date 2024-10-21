package server.JoinGame;

public class JoinGameRequest {
    String playerColor;
    int gameID;

    public String getPlayerColor() {
        return this.playerColor;
    }

    public int getGameID() {
        return this.gameID;
    }
}

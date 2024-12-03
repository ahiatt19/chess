package handler.obj;

import chess.ChessGame;

public class UpdateGameRequest {
    int gameID;
    ChessGame game;

    public UpdateGameRequest(int gameID, ChessGame game) {
        this.gameID = gameID;
        this.game = game;
    }

    public ChessGame getGame() {
        return this.game;
    }

    public int getGameID() {
        return this.gameID;
    }
}

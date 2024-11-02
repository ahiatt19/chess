package server.listgames;

import chess.ChessGame;

public record ListGamesData(int gameID, String whiteUsername, String blackUsername, String gameName) {}
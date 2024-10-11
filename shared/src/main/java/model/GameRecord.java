package model;

import chess.ChessGame;

record GameRecord(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {}
package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.Service;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotficationMessage;

import java.util.Collection;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Service service;

    public WebSocketHandler(Service service) {
        this.service = service;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
            makeMove(session, makeMoveCommand.getAuthToken(), makeMoveCommand.getGameID(), makeMoveCommand.getMove());
        }

        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(session, userGameCommand.getAuthToken(), userGameCommand.getGameID());
            case RESIGN -> resign(session, userGameCommand.getAuthToken(), userGameCommand.getGameID());
            case LEAVE -> leave(userGameCommand.getAuthToken(), userGameCommand.getGameID());
        }
    }

    private void connect(Session session, String authToken, int gameID) throws Exception{
        GameData gameData = service.getGame(authToken, gameID);
        if (gameData != null) {
            connections.add(gameID, authToken, session);
            String username = service.getUsername(authToken);
            ChessGame.TeamColor userColor = null;
            if (Objects.equals(gameData.whiteUsername(), username)) {
                userColor = ChessGame.TeamColor.WHITE;
            } else if (Objects.equals(gameData.blackUsername(), username)) {
                userColor = ChessGame.TeamColor.BLACK;
            }
            var message = "Something was Entered Incorrectly";
            if (userColor != null) {
                message = String.format("%s joined as player %s", username, userColor);
            } else {
                message = String.format("%s joined as an observer", username);
            }

            var loadGame = new LoadGameMessage(gameData);
            connections.send(authToken, loadGame);


            var notification = new NotficationMessage(message);
            connections.broadcast(authToken, notification, gameID);
        } else {
            var error = new ErrorMessage("That Game Does Not Exist.");
            Gson gson = new Gson();
            var json = gson.toJson(error);
            session.getRemote().sendString(json);
        }
    }

    private void resign(Session session, String authToken, int gameID) throws Exception{
        String username = service.getUsername(authToken);
        GameData gameData = service.getGame(authToken, gameID);
        if (gameData.game().getGameOver()) {
            errMessage(session, "The Game is Over.");
            return;
        }
        ChessGame.TeamColor userColor = null;
        if (Objects.equals(gameData.whiteUsername(), username)) {
            userColor = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(gameData.blackUsername(), username)) {
            userColor = ChessGame.TeamColor.BLACK;
        }
        if (userColor != null) {
            gameData.game().setGameOver(true);
            service.updateAGame(authToken, gameID, gameData.game());


            var message = String.format("%s resigned from the game", username);
            var notification = new NotficationMessage(message);
            connections.broadcast("", notification, gameID);

            connections.remove(authToken);
        } else {
            errMessage(session, "You Cannot Resign as an Observer");
        }
    }

    private void leave(String authToken, int gameID) throws Exception {
        String username = service.getUsername(authToken);
        GameData gameData = service.getGame(authToken, gameID);

        ChessGame.TeamColor userColor = null;
        if (Objects.equals(gameData.whiteUsername(), username)) {
            userColor = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(gameData.blackUsername(), username)) {
            userColor = ChessGame.TeamColor.BLACK;
        }
        if (userColor != null) {
            service.leaveGame(gameID, userColor);

            var message = String.format("Player %s Left the Game", username);
            var notification = new NotficationMessage(message);
            connections.broadcast(authToken, notification, gameID);
            connections.remove(authToken);
        } else {
            var message = String.format("Observer %s Left the Game", username);
            var notification = new NotficationMessage(message);
            connections.broadcast(authToken, notification, gameID);
            connections.remove(authToken);
        }
    }

    private void makeMove(Session session, String authToken, int gameID, ChessMove move) throws Exception {
        if (service.getGame(authToken, gameID) != null) {
            GameData game = service.getGame(authToken, gameID);
            if (game.game().getGameOver()) {
                errMessage(session, "The Game is Over.");
                return;
            }

            boolean isGoodMove = false;
            Collection<ChessMove> validMoves = game.game().validMoves(move.getStartPosition());
            for (ChessMove move1 : validMoves) {
                if (move1.getEndPosition().getRow() == move.getEndPosition().getRow() &&
                        move1.getEndPosition().getColumn() == move.getEndPosition().getColumn()) {
                    isGoodMove = true;
                    break;
                }
            }
            if (isGoodMove) {
                ChessGame.TeamColor pieceColor = game.game().getBoard().getPiece(move.getStartPosition()).getTeamColor();
                if (pieceColor != game.game().getTeamTurn()) {
                    errMessage(session, "It is " + game.game().getTeamTurn() + "'s turn");
                    return;
                }
                String username = service.getUsername(authToken);

                ChessGame.TeamColor userColor = null;
                if (Objects.equals(game.whiteUsername(), username)) {
                    userColor = ChessGame.TeamColor.WHITE;
                } else if (Objects.equals(game.blackUsername(), username)) {
                    userColor = ChessGame.TeamColor.BLACK;
                }

                if (pieceColor != userColor) {
                    errMessage(session, "You May Only Move " + userColor + " Pieces.");
                    return;
                }

                game.game().makeMove(move);

                String startCoor = move.getStartPosition().getRow() + letterCoor(move.getStartPosition().getColumn());
                String endCoor = move.getEndPosition().getRow() + letterCoor(move.getEndPosition().getColumn());
                var message = String.format("%s made the move %s to %s", username, startCoor, endCoor);
                var notification = new NotficationMessage(message);
                connections.broadcast(authToken, notification, gameID);

                var gameMessage = "";
                if (game.game().isInCheckmate(game.game().getTeamTurn())) {
                    gameMessage = String.format("%s is in CheckMate, %s Has Won", game.game().getTeamTurn(), username);
                    game.game().setGameOver(true);
                    var notification1 = new NotficationMessage(gameMessage);
                    connections.broadcast("", notification1, gameID);
                } else if (game.game().isInStalemate(game.game().getTeamTurn())) {
                    gameMessage = String.format("The Game is in Stalemate, Game Over");
                    game.game().setGameOver(true);
                    var notification2 = new NotficationMessage(gameMessage);
                    connections.broadcast("", notification2, gameID);
                } else if (game.game().isInCheck(game.game().getTeamTurn())) {
                    gameMessage = String.format("%s is in Check", game.game().getTeamTurn());
                    var notification3 = new NotficationMessage(gameMessage);
                    connections.broadcast("", notification3, gameID);
                }

                service.updateAGame(authToken, gameID, game.game());
                //load game to all in session
                var loadGame = new LoadGameMessage(game);
                connections.broadcast("", loadGame, gameID);
            } else {
                errMessage(session, "The Move was not Valid");
            }
        } else {
            errMessage(session, "Not Authorized to Make a Move");
        }
    }

    public void errMessage(Session session, String message) throws Exception {
        var error = new ErrorMessage(message);
        Gson gson = new Gson();
        var json = gson.toJson(error);
        session.getRemote().sendString(json);
    }

    public String letterCoor(int itr) {
        return switch (itr) {
            case 1 -> "a";
            case 2 -> "b";
            case 3 -> "c";
            case 4 -> "d";
            case 5 -> "e";
            case 6 -> "f";
            case 7 -> "g";
            case 8 -> "h";
            default -> throw new IllegalStateException("Unexpected coordinate: " + itr);
        };
    }
}

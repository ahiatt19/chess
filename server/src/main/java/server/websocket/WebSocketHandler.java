package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import handler.obj.JoinGameRequest;
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
        System.out.println("In on Message of WSHandler");
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
            makeMove(session, makeMoveCommand.getAuthToken(), makeMoveCommand.getGameID(), makeMoveCommand.getMove());
        }

        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(session, userGameCommand.getAuthToken(), userGameCommand.getGameID());
            case RESIGN -> resign(session, userGameCommand.getAuthToken(), userGameCommand.getGameID());
            case LEAVE -> leave(session, userGameCommand.getAuthToken(), userGameCommand.getGameID());
        }
    }

    private void connect(Session session, String authToken, int gameID) throws Exception{
        if (service.getGame(authToken, gameID) != null) {
            connections.add(gameID, authToken, session);
            String username = service.getUsername(authToken);

            GameData gameData = service.getGame(authToken, gameID);

            var loadGame = new LoadGameMessage(gameData);
            connections.send(authToken, loadGame);

            var message = String.format("%s joined the game", username);
            var notification = new NotficationMessage(message);
            connections.broadcastNotification(authToken, notification);
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
            connections.broadcastNotification("", notification);

            connections.remove(authToken);
        } else {
            errMessage(session, "You Cannot Resign as an Observer");
        }
    }

    private void leave(Session session, String authToken, int gameID) throws Exception {
        System.out.println("leave");
        String username = service.getUsername(authToken);
        GameData gameData = service.getGame(authToken, gameID);

        ChessGame.TeamColor userColor = null;
        if (Objects.equals(gameData.whiteUsername(), username)) {
            userColor = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(gameData.blackUsername(), username)) {
            userColor = ChessGame.TeamColor.BLACK;
        }
        if (userColor != null) {
            System.out.println("we be in this ho");
            service.leaveGame(gameID, userColor);

            var message = String.format("Player %s left the game", username);
            var notification = new NotficationMessage(message);
            connections.broadcastNotification(authToken, notification);
            connections.remove(authToken);
        } else {

            var message = String.format("Observer %s left the game", username);
            var notification = new NotficationMessage(message);
            connections.broadcastNotification(authToken, notification);
            connections.remove(authToken);
        }
    }

    private void makeMove(Session session, String authToken, int gameID, ChessMove move) throws Exception {
        System.out.println("MAKING MOVES B");
        if (service.getGame(authToken, gameID) != null) {
            GameData game = service.getGame(authToken, gameID);
            System.out.println(game.game().getGameOver());
            if (game.game().getGameOver()) {
                errMessage(session, "The Game is Over.");
                return;
            }

            boolean goodMove = false;
            //System.out.println(game);
            Collection<ChessMove> validMoves = game.game().validMoves(move.getStartPosition());
            for (ChessMove m : validMoves) {
                if (m.getEndPosition().getRow() == move.getEndPosition().getRow() &&
                        m.getEndPosition().getColumn() == move.getEndPosition().getColumn()) {
                    goodMove = true;
                    break;
                }
            }
            if (goodMove) {
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
                service.updateAGame(authToken, gameID, game.game());

                //load game to all in session
                var loadGame = new LoadGameMessage(game);
                connections.broadcastNotification("", loadGame);

                String startCoor = move.getStartPosition().getRow() + letterCoor(move.getStartPosition().getColumn());
                String endCoor = move.getEndPosition().getRow() + letterCoor(move.getEndPosition().getColumn());

                //notification to all others

                var message = String.format("%s made the move %s to %s", username, startCoor, endCoor);
                var notification = new NotficationMessage(message);
                connections.broadcastNotification(authToken, notification);
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

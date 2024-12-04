package server.websocket;

import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.Service;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotficationMessage;
import websocket.messages.ServerMessage;

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

        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(session, userGameCommand.getAuthToken(), userGameCommand.getGameID());
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
}

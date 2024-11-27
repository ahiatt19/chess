package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.Service;
import websocket.commands.UserGameCommand;
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
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        String username = service.getUsername(command.getAuthToken());

        connections.add(command.getGameID(), command.getAuthToken(), session);

        switch (command.getCommandType()) {
            case CONNECT -> connect(session, username, command);
        }
    }

    private void connect(Session session, String username, UserGameCommand command) throws Exception{
        System.out.println("Inside connect");
        var message = String.format("%s joined as a player", username);
        System.out.println(message);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, message);
        connections.broadcast(command.getAuthToken(), serverMessage);
    }
}

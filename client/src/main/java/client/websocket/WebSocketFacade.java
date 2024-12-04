package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageHandler handler;
    
    public WebSocketFacade(String url, ServerMessageHandler handler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.handler = handler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    System.out.println("On Message");
                    System.out.println(message);

                    // ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    handler.notify(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID) throws Exception {
        try {
            System.out.println("In connect in Web Socket Facade");
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            System.out.println(userGameCommand);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws Exception {
        try {
            System.out.println("In makeMove in Web Socket Facade");
            MakeMoveCommand makeMoveCommand = new MakeMoveCommand(authToken, gameID, move);
            System.out.println(makeMoveCommand);
            this.session.getBasicRemote().sendText(new Gson().toJson(makeMoveCommand));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }
}

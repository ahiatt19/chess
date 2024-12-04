package client.websocket;

import websocket.messages.NotficationMessage;
import websocket.messages.ServerMessage;

public interface ServerMessageHandler {
    void notify(ServerMessage message);
}

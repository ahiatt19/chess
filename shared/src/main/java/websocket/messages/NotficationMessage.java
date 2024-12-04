package websocket.messages;

public class NotficationMessage extends ServerMessage {
    String message;

    public NotficationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

package handler.obj;

public class CreateGameRequest {
    String gameName;

    public CreateGameRequest (String gameName) {
        this.gameName = gameName;
    }
    public String getGameName() {
        return this.gameName;
    }
}
package server;

import model.AuthData;

public class RegisterResult {
    String username;
    String authToken;

    public RegisterResult(AuthData auth) {
        this.username = auth.username();
        this.authToken = auth.authToken();
    }
}

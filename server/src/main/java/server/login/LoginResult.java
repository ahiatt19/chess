package server.login;

import model.AuthData;

public class LoginResult {
    String username;
    String authToken;

    public LoginResult(AuthData auth) {
        this.username = auth.username();
        this.authToken = auth.authToken();
    }
}

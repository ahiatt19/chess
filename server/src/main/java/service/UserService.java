package service;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import model.GameData;
import server.CreateGame.CreateGameResult;
import server.JoinGame.JoinGameRequest;
import server.ListGames.ListGamesResult;
import server.Register.RegisterResult;
import server.Register.RegisterRequest;
import server.Login.LoginResult;
import server.Login.LoginRequest;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;
    private final GameDAO gameDataAccess;

    public UserService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        UserData userData = new UserData(request.getUsername(), request.getPassword(), request.getEmail());

        UserData user = userDataAccess.getUser(request.getUsername());
        if (user == null) {
            System.out.println("User is null: " + request.getUsername());
            userDataAccess.createUser(userData);
            AuthData auth = new AuthData(request.getUsername(), UUID.randomUUID().toString());
            authDataAccess.createAuth(auth);
            return new RegisterResult(auth);
        } else {
            return null;
        }
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        UserData user = userDataAccess.getUser(request.getUsername());
        if (user == null) {
            return null;
        }
        if (Objects.equals(user.password(), request.getPassword())) {
            AuthData auth = new AuthData(request.getUsername(), UUID.randomUUID().toString());
            authDataAccess.createAuth(auth);
            return new LoginResult(auth);
        } else {
            return null;
        }
    }

    public String logout(String authToken) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        //System.out.println(authData);
        if (authData != null) {
            authDataAccess.deleteAuth(authData.authToken());
            return null;
        } else {
            return "401";
        }
    }

    public CreateGameResult createGame(String gameName, String authToken) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        System.out.println("The auth Data: " + authData);
        if (authData != null) {

            GameData gameData = gameDataAccess.createGame(gameName);
            return new CreateGameResult(gameData.gameID());
        } else {
            return null;
        }
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        System.out.println("The auth Data: " + authData);
        if (authData != null) {
            System.out.println("RETURNING in here");
            return new ListGamesResult(gameDataAccess.listGames());
        }
        System.out.println("RETURNING NULL HERE");
        return null;
    }

    public String updateGame(String authToken, JoinGameRequest joinGameRequest) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        System.out.println("The auth Data: " + authData);
        if (authData != null) {
            GameData gameData = gameDataAccess.getGame(joinGameRequest.getGameID());

            if (gameData != null && Objects.equals(joinGameRequest.getPlayerColor(), "WHITE")) {
                if (gameData.whiteUsername() != null) {
                    return "403";
                }
                GameData updatedGameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.game());
                gameDataAccess.updateGame(updatedGameData);
                return null;
            } else if (gameData != null && Objects.equals(joinGameRequest.getPlayerColor(), "BLACK")) {
                System.out.println("black username: "+ gameData.blackUsername());
                if (gameData.blackUsername() != null) {

                    return "403";
                }
                GameData updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.game());
                System.out.println(updatedGameData);
                gameDataAccess.updateGame(updatedGameData);
                return null;
            }
            return "400";
        }
        return "401";
    }

    public void clear() throws DataAccessException {
        authDataAccess.clearAuths();
        userDataAccess.clearUsers();
        gameDataAccess.clearGames();
    }
}

package service;
import chess.ChessGame;
import dataaccess.*;
import handler.obj.*;
import model.UserData;
import model.AuthData;
import model.GameData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;
import java.util.UUID;

public class Service {

    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;
    private final GameDAO gameDataAccess;

    public Service(MySQLGameDAO dataAccess) {
        this.userDataAccess = dataAccess;
        this.authDataAccess = dataAccess;
        this.gameDataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        UserData userData = new UserData(request.getUsername(), request.getPassword(), request.getEmail());
        UserData user = userDataAccess.getUser(request.getUsername());
        if (user == null) {
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
        if (BCrypt.checkpw(request.getPassword(), user.password())) {
            AuthData auth = new AuthData(request.getUsername(), UUID.randomUUID().toString());
            authDataAccess.createAuth(auth);
            return new LoginResult(auth);
        } else {
            return null;
        }
    }

    public String logout(String authToken) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        if (authData != null) {
            authDataAccess.deleteAuth(authData.authToken());
            return null;
        } else {
            return "401";
        }
    }

    public CreateGameResult createGame(String gameName, String authToken) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        if (authData != null) {
            int gameID = gameDataAccess.createGame(gameName);
            return new CreateGameResult(gameID);
        } else {
            return null;
        }
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        if (authData != null) {
            return new ListGamesResult(gameDataAccess.listGames());
        }
        return null;
    }

    public String joinGame(String authToken, JoinGameRequest joinGameRequest) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        if (authData != null) {
            GameData gameData = gameDataAccess.getGame(joinGameRequest.getGameID());
            if (gameData != null) {
                if (Objects.equals(joinGameRequest.getPlayerColor(), "WHITE")) {
                    if (gameData.whiteUsername() != null) {
                        return "403";
                    }
                    GameData updatedGameData = new GameData(gameData.gameID(), authData.username(),
                            gameData.blackUsername(), gameData.gameName(), gameData.game());
                    gameDataAccess.joinGame(updatedGameData);
                    return null;
                } else if (gameData != null && Objects.equals(joinGameRequest.getPlayerColor(), "BLACK")) {
                    if (gameData.blackUsername() != null) {
                        return "403";
                    }
                    GameData updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(),
                            authData.username(), gameData.gameName(), gameData.game());
                    gameDataAccess.joinGame(updatedGameData);
                    return null;
                }
            }
            return "400";
        }
        return "401";
    }

    public String updateAGame(String authToken, int gameID, ChessGame chessGame) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        if (authData != null) {
            gameDataAccess.updateGame(gameID, chessGame);
            return null;
        }
        return "401";
    }

    public GameData getGame(String authToken, int gameID) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        if (authData != null) {
            return gameDataAccess.getGame(gameID);
        } else {
            return null;
        }
    }

    public void clear() throws DataAccessException {
        authDataAccess.clearAuths();
        userDataAccess.clearUsers();
        gameDataAccess.clearGames();
    }

    //Creating for phase six to get the username
    public String getUsername(String authToken) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        return authData.username();
    }

    public void leaveGame(int gameID, ChessGame.TeamColor color) throws DataAccessException {
        gameDataAccess.leaveGame(gameID, color);
    }
}

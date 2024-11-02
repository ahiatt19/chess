package dataaccess;

import chess.ChessGame;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryGameDAO implements UserDAO, AuthDAO, GameDAO {

    private ArrayList<UserData> currentUsers = new ArrayList<>();
    private ArrayList<AuthData> usersAuth = new ArrayList<>();
    private ArrayList<GameData> currentGames = new ArrayList<>();
    private int nextGameID = 1;


    public void clearUsers() {
        currentUsers = new ArrayList<>();
    }


    public void clearAuths() {
        usersAuth = new ArrayList<>();
    }


    public void clearGames() {
        currentGames = new ArrayList<>();
    }


    public void createUser(UserData u) {
        currentUsers.add(u);
    }


    public UserData getUser(String username) {
        int i = 0;
        UserData user = null;
        while (i < currentUsers.size()) {
            if (Objects.equals(currentUsers.get(i).username(), username)) {
                user = currentUsers.get(i);
            }
            i++;
        }
        return user;
    }


    public void createAuth(AuthData a) {
        usersAuth.add(a);
    }


    public AuthData getAuth(String authToken) {
        int i = 0;
        AuthData authData = null;
        while (i < usersAuth.size()) {
            if (Objects.equals(usersAuth.get(i).authToken(), authToken)) {
                authData = usersAuth.get(i);
            }
            i++;
        }
        return authData;
    }


    public void deleteAuth(String authToken) {
        int i = 0;
        while (i < usersAuth.size()) {
            if (Objects.equals(usersAuth.get(i).authToken(), authToken)) {
                usersAuth.remove(i);
            }
            i++;
        }
    }


    public int createGame(String gameName) {
        GameData gameData = new GameData(nextGameID++, null, null, gameName, new ChessGame());
        currentGames.add(gameData);
        return gameData.gameID();
    }


    public ArrayList<GameData> listGames() {
        return currentGames;
    }


    public GameData getGame(int gameID) {
        int i = 0;
        GameData gameData = null;
        while (i < currentGames.size()) {
            if (Objects.equals(currentGames.get(i).gameID(), gameID)) {
                gameData = currentGames.get(i);
            }
            i++;
        }
        return gameData;
    }


    public void updateGame(GameData gameData) {
        int i = 0;
        while (i < currentGames.size()) {
            if (Objects.equals(currentGames.get(i).gameID(), gameData.gameID())) {
                currentGames.set(i, gameData);
            }
            i++;
        }
    }

    //Created for testing
    public int userSize() {
        return currentUsers.size();
    }

    //Created for testing
    public int gamesSize() {
        return currentGames.size();
    }

    //Created for testing
    public int authSize() {
        return usersAuth.size();
    }
}

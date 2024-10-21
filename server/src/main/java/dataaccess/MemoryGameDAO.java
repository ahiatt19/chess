package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryGameDAO implements UserDAO, AuthDAO, GameDAO {

    private ArrayList<UserData> currentUsers = new ArrayList<>();
    private Map<String, String> usersAuth = new HashMap<>();
    private ArrayList<GameData> currentGames = new ArrayList<>();
    private int nextGameID = 1;


    public void clearUsers() {
        currentUsers = new ArrayList<>();
    }

    public void clearAuths() {
        usersAuth = new HashMap<>();
    }

    public void clearGames() {
        currentGames = new ArrayList<>();
    }

    //to create/register user
    public void createUser(UserData u) {
        currentUsers.add(u);
    }

    public UserData getUser(String username) {
        int i = 0;
        UserData user = null;
        while (i < currentUsers.size()) {
            System.out.println("List username: " + currentUsers.get(i).username() + " our username: " + username);
            if (Objects.equals(currentUsers.get(i).username(), username)) {
                user = currentUsers.get(i);
            }
            i++;
        }
        System.out.println("This is user: " + user);
        return user;
    }


    public void createAuth(AuthData a) {
        usersAuth.put(a.authToken(), a.username());
    }

    public AuthData getAuth(String authToken) {
        if (!usersAuth.containsKey(authToken))
            return null;
        String username = usersAuth.get(authToken);
        return new AuthData(username, authToken);
    }

    public void deleteAuth(String username) {
        usersAuth.remove(username);
    }

    public GameData createGame(String gameName) {
        GameData gameData = new GameData(nextGameID++, null, null, gameName, new ChessGame());
        currentGames.add(gameData);
        return gameData;
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
}

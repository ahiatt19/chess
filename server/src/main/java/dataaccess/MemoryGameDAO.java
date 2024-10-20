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

    final private ArrayList<UserData> currentUsers = new ArrayList<>();
    final private Map<String, String> usersAuth = new HashMap<>();
    final private ArrayList<GameData> currentGames = new ArrayList<>();
    private int nextGameID = 1;

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
}

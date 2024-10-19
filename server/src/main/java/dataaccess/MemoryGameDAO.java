package dataaccess;

import model.AuthData;
import model.UserData;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryGameDAO implements UserDAO, AuthDAO {

    final private ArrayList<UserData> currentUsers = new ArrayList<>();
    final private Map<String, String> usersAuth = new HashMap<>();

    //to create/register user
    public void createUser(UserData u) throws DataAccessException {
        currentUsers.add(u);
        //System.out.println(currentUsers);
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

    public void createAuth(AuthData a) throws DataAccessException {
        usersAuth.put(a.username(), a.authToken());
    }

    public AuthData getAuth(String username) throws DataAccessException {
        String authToken = usersAuth.get(username);
        return new AuthData(username, authToken);
    }
}

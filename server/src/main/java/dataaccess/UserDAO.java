package dataaccess;

import model.UserData;


public interface UserDAO {
    void createUser(UserData u);

    UserData getUser(String username);

    void clearUsers();
}



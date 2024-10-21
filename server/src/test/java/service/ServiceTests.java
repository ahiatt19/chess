package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.ListGames.ListGamesResult;
import server.Login.LoginRequest;
import server.Login.LoginResult;
import server.Register.RegisterRequest;
import server.Register.RegisterResult;
import dataaccess.MemoryGameDAO;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServiceTests {

    MemoryGameDAO memory = new MemoryGameDAO();
    Service service = new Service(memory);

    @Test
    @Order(1)
    @DisplayName("Register User +")
    public void registerUser() throws DataAccessException {
        RegisterRequest regRequest = new RegisterRequest("u", "p", "e");

        service.register(regRequest);

        Assertions.assertEquals("u", memory.getUser("u").username());
        Assertions.assertEquals(1, memory.userSize());
        Assertions.assertEquals(1, memory.authSize());
    }

    @Test
    @Order(2)
    @DisplayName("Register User -")
    public void reregisterUser() throws DataAccessException {
        //Fine request
        RegisterRequest goodRequest = new RegisterRequest("UniqueUser22", "2222", "email!");

        service.register(goodRequest);

        //user should be registered
        Assertions.assertEquals("UniqueUser22", memory.getUser("UniqueUser22").username());
        Assertions.assertEquals(1, memory.userSize());
        Assertions.assertEquals(1, memory.authSize());

        //Try to use the same username
        RegisterRequest badRequest = new RegisterRequest("UniqueUser22", "6767", "other!");

        //Try to register same username
        service.register(badRequest);

        //Still only one User registered
        Assertions.assertEquals("UniqueUser22", memory.getUser("UniqueUser22").username());
        Assertions.assertEquals(1, memory.userSize());
        Assertions.assertEquals(1, memory.authSize());
    }

    @Test
    @Order(3)
    @DisplayName("Login User +")
    public void loginUser() throws DataAccessException {
        //Create the User Request and create a user
        RegisterRequest goodRequest = new RegisterRequest("GoodUser", "pass", "email");
        service.register(goodRequest);

        service.login(new LoginRequest("GoodUser", "pass"));

        //User in memory is the same User
        Assertions.assertEquals("GoodUser", memory.getUser("GoodUser").username());
        //Still only one user in memory
        Assertions.assertEquals(1, memory.userSize());
        //There should be two authTokens for the user authenticated
        Assertions.assertEquals(2, memory.authSize());
    }

    @Test
    @Order(4)
    @DisplayName("Login User -")
    public void loginBadUser() throws DataAccessException {
        //Create the User Request and create a user
        RegisterRequest goodRequest = new RegisterRequest("GoodUser", "pass", "email");
        service.register(goodRequest);

        //Login user with wrong password
        service.login(new LoginRequest("GoodUser", "wrong pass"));

        //There should be only one authToken for the user authenticated
        Assertions.assertNotEquals(2, memory.authSize());
    }


    @Test
    @Order(5)
    @DisplayName("Logout User +")
    public void logoutUser() throws DataAccessException {
        //Create the User Request and create a user
        RegisterRequest goodRequest = new RegisterRequest("GoodUser", "pass", "email");
        //Register and log in the user
        RegisterResult regResult = service.register(goodRequest);

        //User in memory is the same User
        Assertions.assertEquals("GoodUser", memory.getUser("GoodUser").username());
        //Still only one user in memory
        Assertions.assertEquals(1, memory.userSize());
        //There should be only one authTokens for the user authenticated
        Assertions.assertEquals(1, memory.authSize());

        service.logout(regResult.getAuthToken());

        //User in memory is the same User
        Assertions.assertEquals("GoodUser", memory.getUser("GoodUser").username());
        //Still only one user in memory
        Assertions.assertEquals(1, memory.userSize());
        //There is no auth for that user because we logged them out
        Assertions.assertEquals(0, memory.authSize());
    }


    @Test
    @Order(6)
    @DisplayName("Logout User -")
    public void logoutBadUser() throws DataAccessException {
        RegisterRequest goodRequest = new RegisterRequest("GoodUser", "pass", "email");
        RegisterResult regResult = service.register(goodRequest);

        //Still only one user in memory
        Assertions.assertEquals(1, memory.userSize());
        //There should be only one authTokens for the user authenticated
        Assertions.assertEquals(1, memory.authSize());

        //logout with wrong auth
        service.logout("Incorrect-auth-Token");

        //Still only one user in memory
        Assertions.assertEquals(1, memory.userSize());
        //There is not 0 auths because it failed
        Assertions.assertNotEquals(0, memory.authSize());
    }


    @Test
    @Order(7)
    @DisplayName("Create Game +")
    public void createGameTest() throws DataAccessException {
        RegisterRequest goodRequest = new RegisterRequest("GoodUser", "pass", "email");
        RegisterResult regResult = service.register(goodRequest);

        //create game with auth token
        service.createGame("Game400", regResult.getAuthToken());

        //Still only one user in memory
        Assertions.assertEquals(1, memory.gamesSize());
    }

    @Test
    @Order(8)
    @DisplayName("Create Game -")
    public void createBadGameTest() throws DataAccessException {
        //create game without a valid auth token
        service.createGame("Game400", "This-is-not-an-authToken");

        //Should have no games
        Assertions.assertNotEquals(1, memory.gamesSize());
    }


    @Test
    @Order(9)
    @DisplayName("Create Game +")
    public void listGamesTest() throws DataAccessException {
        RegisterRequest goodRequest = new RegisterRequest("GoodUser", "pass", "email");
        RegisterResult regResult = service.register(goodRequest);

        //Create 3 games
        service.createGame("Game400", regResult.getAuthToken());
        service.createGame("Game6700", regResult.getAuthToken());
        service.createGame("Woah!", regResult.getAuthToken());

        ListGamesResult listGames = service.listGames(regResult.getAuthToken());

        //Still only one user in memory
        Assertions.assertEquals(3, listGames.getGames().size());
    }
}

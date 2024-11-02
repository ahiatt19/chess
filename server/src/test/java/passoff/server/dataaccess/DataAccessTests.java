package passoff.server.dataaccess;

import chess.*;
import dataaccess.DataAccessException;
import dataaccess.MySQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.*;
import server.creategame.CreateGameResult;
import server.joingame.JoinGameRequest;
import server.listgames.ListGamesResult;
import server.login.LoginRequest;
import server.register.RegisterRequest;
import server.register.RegisterResult;
import service.Service;

public class DataAccessTests {

    MySQLGameDAO dataAccess = new MySQLGameDAO();
    Service service = new Service(dataAccess);

    @BeforeEach
    public void setup() throws DataAccessException {
        service.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Register User +")
    public void registerUser() throws DataAccessException {
        RegisterRequest regRequest = new RegisterRequest("username", "password", "email");
        RegisterRequest regRequest2 = new RegisterRequest("username2", "password2", "email");

        service.register(regRequest);
        service.register(regRequest2);

        Assertions.assertEquals("username", dataAccess.getUser("username").username());
        Assertions.assertEquals("username2", dataAccess.getUser("username2").username());
        Assertions.assertEquals(2, dataAccess.userSize());
        Assertions.assertEquals(2, dataAccess.authSize());
    }

    @Test
    @Order(2)
    @DisplayName("Register User -")
    public void reregisterUser() throws DataAccessException {
        //Fine request
        RegisterRequest goodRequest = new RegisterRequest("UniqueUser22", "2222", "email!");

        service.register(goodRequest);

        //user should be registered
        Assertions.assertEquals("UniqueUser22", dataAccess.getUser("UniqueUser22").username());
        Assertions.assertEquals(1, dataAccess.userSize());
        Assertions.assertEquals(1, dataAccess.authSize());

        //Try to use the same username
        RegisterRequest badRequest = new RegisterRequest("UniqueUser22", "6767", "other!");

        //Try to register same username
        service.register(badRequest);

        //Still only one User registered
        Assertions.assertEquals("UniqueUser22", dataAccess.getUser("UniqueUser22").username());
        Assertions.assertEquals(1, dataAccess.userSize());
        Assertions.assertEquals(1, dataAccess.authSize());
    }

    @Test
    @Order(3)
    @DisplayName("Login User +")
    public void loginUser() throws DataAccessException {
        //Create the User Request and create a user
        service.register(new RegisterRequest("GoodUser", "pass", "email"));

        service.login(new LoginRequest("GoodUser", "pass"));

        //User in memory is the same User
        Assertions.assertEquals("GoodUser", dataAccess.getUser("GoodUser").username());
        //Still only one user in memory
        Assertions.assertEquals(1, dataAccess.userSize());
        //There should be two authTokens for the user authenticated
        Assertions.assertEquals(2, dataAccess.authSize());
    }

    @Test
    @Order(4)
    @DisplayName("Login User -")
    public void loginBadUser() throws DataAccessException {
        //Create the User Request and create a user
        RegisterResult regResult = service.register(new RegisterRequest("GoodUser", "pass", "email"));

        //Login user with wrong password
        service.login(new LoginRequest("GoodUser", "wrong pass"));

        //There should be only one authToken for the user authenticated
        Assertions.assertNotEquals(2, dataAccess.authSize());
    }


    @Test
    @Order(5)
    @DisplayName("Logout User +")
    public void logoutUser() throws DataAccessException {
        //Create the User Request and create a user
        RegisterResult regResult = service.register(new RegisterRequest("GoodUser", "pass", "email"));

        //User in memory is the same User
        Assertions.assertEquals("GoodUser", dataAccess.getUser("GoodUser").username());
        //Still only one user in memory
        Assertions.assertEquals(1, dataAccess.userSize());
        //There should be only one authTokens for the user authenticated
        Assertions.assertEquals(1, dataAccess.authSize());

        service.logout(regResult.getAuthToken());

        //User in memory is the same User
        Assertions.assertEquals("GoodUser", dataAccess.getUser("GoodUser").username());
        //Still only one user in memory
        Assertions.assertEquals(1, dataAccess.userSize());
        //There is no auth for that user because we logged them out
        Assertions.assertEquals(0, dataAccess.authSize());
    }


    @Test
    @Order(6)
    @DisplayName("Logout User -")
    public void logoutBadUser() throws DataAccessException {
        RegisterResult regResult = service.register(new RegisterRequest("GoodUser", "pass", "email"));

        //Still only one user in memory
        Assertions.assertEquals(1, dataAccess.userSize());
        //There should be only one authTokens for the user authenticated
        Assertions.assertEquals(1, dataAccess.authSize());

        //logout with wrong auth
        service.logout("Incorrect-auth-Token");

        //Still only one user in memory
        Assertions.assertEquals(1, dataAccess.userSize());
        //There is not 0 auths because it failed
        Assertions.assertNotEquals(0, dataAccess.authSize());
    }


    @Test
    @Order(7)
    @DisplayName("Create Game +")
    public void createGameTest() throws DataAccessException {
        RegisterResult regResult = service.register(new RegisterRequest("GoodUser", "pass", "email"));

        //create game with auth token
        CreateGameResult result = service.createGame("Game400", regResult.getAuthToken());

        //white user is empty
        Assertions.assertEquals(null, dataAccess.getGame(result.getGameID()).whiteUsername());
        //black user is empty
        Assertions.assertEquals(null, dataAccess.getGame(result.getGameID()).blackUsername());
        //check the game is the right type
        Assertions.assertEquals(new ChessGame(), dataAccess.getGame(result.getGameID()).game());
        Assertions.assertEquals(1, dataAccess.gamesSize());
    }

    @Test
    @Order(8)
    @DisplayName("Create Game -")
    public void createBadGameTest() throws DataAccessException {
        //create game without a valid auth token
        service.createGame("Game400", "This-is-not-an-authToken");

        //Should have no games
        Assertions.assertNotEquals(1, dataAccess.gamesSize());


        //create game without a valid auth token
        service.createGame("", "This-is-not-an-authToken");

        //Should have no games
        Assertions.assertNotEquals(1, dataAccess.gamesSize());
    }


    @Test
    @Order(9)
    @DisplayName("List Games +")
    public void listGamesTest() throws DataAccessException {
        RegisterResult regResult = service.register(new RegisterRequest("GoodUser", "pass", "email"));

        //Create 3 games
        service.createGame("Game400", regResult.getAuthToken());
        service.createGame("Game6700", regResult.getAuthToken());
        service.createGame("Woah!", regResult.getAuthToken());

        ListGamesResult listGames = service.listGames(regResult.getAuthToken());

        //Still only one user in memory
        Assertions.assertEquals(3, listGames.getGames().size());
    }


    @Test
    @Order(10)
    @DisplayName("List Games -")
    public void listGamesBadTest() throws DataAccessException {
        RegisterResult regResult = service.register(new RegisterRequest("GoodUser", "pass", "email"));

        //Create 3 games
        service.createGame("Game400", regResult.getAuthToken());
        service.createGame("Game6700", regResult.getAuthToken());
        service.createGame("Woah!", regResult.getAuthToken());

        ListGamesResult listGames = service.listGames("this-is-not-the-auth-token");

        //Because the authToken was incorrect, it returns null
        Assertions.assertEquals(null, listGames);
    }


    @Test
    @Order(11)
    @DisplayName("Update Game +")
    public void updateGameTest() throws DataAccessException, InvalidMoveException {
        RegisterResult regResult1 = service.register(new RegisterRequest("FirstUser", "pass", "email"));
        RegisterResult regResult2 = service.register(new RegisterRequest("OtherUser67", "6767", "email"));

        //Create a game
        CreateGameResult createGameResult = service.createGame("FirstGameWHAT!!", regResult1.getAuthToken());

        //join game requests
        JoinGameRequest joinGameRequest1 = new JoinGameRequest("WHITE", createGameResult.getGameID());
        JoinGameRequest joinGameRequest2 = new JoinGameRequest("BLACK", createGameResult.getGameID());

        //add users to game
        //service.updateGame(regResult1.getAuthToken(), joinGameRequest1);
        //service.updateGame(regResult2.getAuthToken(), joinGameRequest2);

        //Check that the users are correctly in memory in the game Data
        //Assertions.assertEquals("OtherUser67", dataAccess.getGame(createGameResult.getGameID()).blackUsername());
        //Assertions.assertEquals("FirstUser", dataAccess.getGame(createGameResult.getGameID()).whiteUsername());


        //WHITE MOVE PAWN
        ChessGame game = dataAccess.getGame(createGameResult.getGameID()).game();
        game.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(4, 1), null));

        //System.out.println(game);


        dataAccess.updateGame(new GameData(createGameResult.getGameID(), "FirstUser", "OtherUser67", "FirstGameWHAT!!", game));

        System.out.println("LAST");

        Assertions.assertEquals(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                dataAccess.getGame(createGameResult.getGameID()).game().getBoard().getPiece(new ChessPosition(4, 1)));


    }


    @Test
    @Order(12)
    @DisplayName("Update Game -")
    public void updateBadGameTest() throws DataAccessException {
        RegisterResult regResult1 = service.register(new RegisterRequest("FirstUser", "pass", "email"));
        RegisterResult regResult2 = service.register(new RegisterRequest("OtherUser67", "6767", "email"));

        //Create a game
        CreateGameResult createGameResult = service.createGame("FirstGameWHAT!!", regResult1.getAuthToken());

        //join game requests
        JoinGameRequest joinGameRequest1 = new JoinGameRequest("WHITE", createGameResult.getGameID());
        JoinGameRequest joinGameRequest2 = new JoinGameRequest("WHITE", createGameResult.getGameID());

        //add users to game
        service.updateGame(regResult1.getAuthToken(), joinGameRequest1);
        service.updateGame(regResult2.getAuthToken(), joinGameRequest2);

        //Check that only First User is white and black is null
        Assertions.assertNotEquals("OtherUser67", dataAccess.getGame(createGameResult.getGameID()).whiteUsername());
        Assertions.assertEquals("FirstUser", dataAccess.getGame(createGameResult.getGameID()).whiteUsername());
        Assertions.assertEquals(null, dataAccess.getGame(createGameResult.getGameID()).blackUsername());
    }


    @Test
    @Order(13)
    @DisplayName("Clear +")
    public void clearTest() throws DataAccessException {
        //Register 2 users
        RegisterResult regResult = service.register(new RegisterRequest("User1", "pass", "email"));
        service.register(new RegisterRequest("User2", "pass", "email"));

        //Create 3 games
        service.createGame("Game400", regResult.getAuthToken());
        service.createGame("Game6700", regResult.getAuthToken());
        service.createGame("Woah!", regResult.getAuthToken());

        //show they were filled before clear
        Assertions.assertEquals(3, dataAccess.gamesSize());
        Assertions.assertEquals(2, dataAccess.userSize());
        Assertions.assertEquals(2, dataAccess.authSize());

        //Create 3 games
        service.clear();

        //all should be size 0
        Assertions.assertEquals(0, dataAccess.gamesSize());
        Assertions.assertEquals(0, dataAccess.userSize());
        Assertions.assertEquals(0, dataAccess.authSize());
    }
}
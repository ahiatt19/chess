package dataaccess;

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
    @DisplayName("Register U +")
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
    @DisplayName("Register U -")
    public void reregisterUser() throws DataAccessException {
        //Fine request
        RegisterRequest betterRequest = new RegisterRequest("AnotherUser!!Yay", "jail", "soooemail!");

        service.register(betterRequest);

        //user should be registered
        Assertions.assertEquals("AnotherUser!!Yay", dataAccess.getUser("AnotherUser!!Yay").username());
        Assertions.assertEquals(1, dataAccess.userSize());
        Assertions.assertEquals(1, dataAccess.authSize());

        //Try to use the same username
        RegisterRequest worseRequest = new RegisterRequest("AnotherUser!!Yay", "90909", "anemail!!");

        //Try to register same username
        service.register(worseRequest);

        Assertions.assertEquals("AnotherUser!!Yay", dataAccess.getUser("AnotherUser!!Yay").username());
        Assertions.assertEquals(1, dataAccess.userSize());
        Assertions.assertEquals(1, dataAccess.authSize());
    }

    @Test
    @Order(3)
    @DisplayName("Login U +")
    public void loginUser() throws DataAccessException {
        //Create the User Request and create a user
        service.register(new RegisterRequest("Thisisagooduser", "passswordddd", "email!!"));

        service.login(new LoginRequest("Thisisagooduser", "passswordddd"));

        //User in memory
        Assertions.assertEquals("Thisisagooduser", dataAccess.getUser("Thisisagooduser").username());
        //Still only one user in memory
        Assertions.assertEquals(1, dataAccess.userSize());
        //There should be two authTokens
        Assertions.assertEquals(2, dataAccess.authSize());
    }

    @Test
    @Order(4)
    @DisplayName("Login U -")
    public void loginBadUser() throws DataAccessException {
        //Create the User Request and create a user
        service.register(new RegisterRequest("HorribleUser", "passWord", "eMAILLL"));

        //wrong password
        service.login(new LoginRequest("HorribleUser", "wrong PASSWORDDDD!!"));

        //There should be only one authToken
        Assertions.assertNotEquals(2, dataAccess.authSize());
    }


    @Test
    @Order(5)
    @DisplayName("Logout U +")
    public void logoutUser() throws DataAccessException {
        //Create the User Request and create a user
        RegisterResult regResult1 = service.register(new RegisterRequest("GOOOOOD", "PASSSSSS", "EMAILLLL"));

        //User in memory
        Assertions.assertEquals("GOOOOOD", dataAccess.getUser("GOOOOOD").username());
        //one user in memory
        Assertions.assertEquals(1, dataAccess.userSize());
        //one authTokens for the user authenticated
        Assertions.assertEquals(1, dataAccess.authSize());

        service.logout(regResult1.getAuthToken());

        //User in memory
        Assertions.assertEquals("GOOOOOD", dataAccess.getUser("GOOOOOD").username());
        //1 user
        Assertions.assertEquals(1, dataAccess.userSize());
        //There is no auth for that user
        Assertions.assertEquals(0, dataAccess.authSize());
    }


    @Test
    @Order(6)
    @DisplayName("Logout U -")
    public void logoutBadUser() throws DataAccessException {
        service.register(new RegisterRequest("GOOOD", "PASSS", "email"));

        //Still only one user in memory
        Assertions.assertEquals(1, dataAccess.userSize());
        //There should be only one authTokens for the user authenticated
        Assertions.assertEquals(1, dataAccess.authSize());

        //wrong auth
        service.logout("THISISWRONGGGGG");

        Assertions.assertEquals(1, dataAccess.userSize());
        Assertions.assertNotEquals(0, dataAccess.authSize());
    }


    @Test
    @Order(7)
    @DisplayName("Create G +")
    public void createGameTest() throws DataAccessException {
        RegisterResult regResult = service.register(new RegisterRequest("GOOOOODD", "PPPPPASS", "EMAILL"));

        //create game with auth token
        CreateGameResult result = service.createGame("GAMENAMEEE:)", regResult.getAuthToken());

        //white user is empty
        Assertions.assertNull(dataAccess.getGame(result.getGameID()).whiteUsername());
        //black user is empty
        Assertions.assertNull(dataAccess.getGame(result.getGameID()).blackUsername());
        //check the game is the right type
        Assertions.assertEquals(new ChessGame(), dataAccess.getGame(result.getGameID()).game());
        Assertions.assertEquals(1, dataAccess.gamesSize());
    }

    @Test
    @Order(8)
    @DisplayName("Create G -")
    public void createBadGameTest() throws DataAccessException {
        //invalid auth
        service.createGame("GAMENAME!", "NOTANAUTH");

        //no games
        Assertions.assertNotEquals(1, dataAccess.gamesSize());


        //create game without a valid auth token
        service.createGame("", "This-is-not-an-authToken");

        //no games
        Assertions.assertNotEquals(1, dataAccess.gamesSize());
    }


    @Test
    @Order(9)
    @DisplayName("List G +")
    public void listGamesTest() throws DataAccessException {
        RegisterResult regResult = service.register(new RegisterRequest("GOOOOOD", "PASSS", "email"));

        //Create 3
        service.createGame("GAME4", regResult.getAuthToken());
        service.createGame("GAME9", regResult.getAuthToken());
        service.createGame("GAME8", regResult.getAuthToken());

        ListGamesResult listGames = service.listGames(regResult.getAuthToken());

        //Still only one user
        Assertions.assertEquals(3, listGames.getGames().size());
    }


    @Test
    @Order(10)
    @DisplayName("List G -")
    public void listGamesBadTest() throws DataAccessException {
        RegisterResult regResult = service.register(new RegisterRequest("GOOD", "PASSSS", "email"));

        //Create 3 games
        service.createGame("GAME1", regResult.getAuthToken());
        service.createGame("GAME2", regResult.getAuthToken());
        service.createGame("GAME3", regResult.getAuthToken());

        ListGamesResult listGames = service.listGames("not an auth!");

        //bad auth
        Assertions.assertNull(listGames);
    }


    @Test
    @Order(11)
    @DisplayName("Update G +")
    public void updateGameTest() throws DataAccessException, InvalidMoveException {
        RegisterResult regResult1 = service.register(new RegisterRequest("FIRST", "PASSSS", "email"));
        RegisterResult regResult2 = service.register(new RegisterRequest("SECOND", "PASSSS", "email"));

        //Create a game
        CreateGameResult createGameResult = service.createGame("FIRSTGAMENAME!!", regResult1.getAuthToken());

        //join game requests
        JoinGameRequest joinGameRequest1 = new JoinGameRequest("WHITE", createGameResult.getGameID());
        JoinGameRequest joinGameRequest2 = new JoinGameRequest("BLACK", createGameResult.getGameID());

        //add users to game
        service.updateGame(regResult1.getAuthToken(), joinGameRequest1);
        service.updateGame(regResult2.getAuthToken(), joinGameRequest2);

        //Check that the users are correctly in memory in the game Data
        Assertions.assertEquals("SECOND", dataAccess.getGame(createGameResult.getGameID()).blackUsername());
        Assertions.assertEquals("FIRST", dataAccess.getGame(createGameResult.getGameID()).whiteUsername());


        //WHITE MOVE PAWN
        ChessGame game = dataAccess.getGame(createGameResult.getGameID()).game();
        game.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(4, 1), null));

        dataAccess.updateGame(new GameData(createGameResult.getGameID(), "FIRST", "SECOND", "FirstGameWHAT!!", game));

        Assertions.assertEquals(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                dataAccess.getGame(createGameResult.getGameID()).game().getBoard().getPiece(new ChessPosition(4, 1)));


    }


    @Test
    @Order(12)
    @DisplayName("Update G -")
    public void updateBadGameTest() throws DataAccessException {
        RegisterResult regResult1 = service.register(new RegisterRequest("FIRST", "pasSSs", "email"));
        RegisterResult regResult2 = service.register(new RegisterRequest("SECOND", "PASSSS", "email"));

        //Create a game
        CreateGameResult createGameResult = service.createGame("firstGAMEEE", regResult1.getAuthToken());

        //cant join same place
        JoinGameRequest joinGameRequest1 = new JoinGameRequest("WHITE", createGameResult.getGameID());
        JoinGameRequest joinGameRequest2 = new JoinGameRequest("WHITE", createGameResult.getGameID());

        //add users to game
        service.updateGame(regResult1.getAuthToken(), joinGameRequest1);
        service.updateGame(regResult2.getAuthToken(), joinGameRequest2);

        //Check that only First User is white
        Assertions.assertNotEquals("SECOND", dataAccess.getGame(createGameResult.getGameID()).whiteUsername());
        Assertions.assertEquals("FIRST", dataAccess.getGame(createGameResult.getGameID()).whiteUsername());
        Assertions.assertNull(dataAccess.getGame(createGameResult.getGameID()).blackUsername());
    }


    @Test
    @Order(13)
    @DisplayName("Clear +")
    public void clearTest() throws DataAccessException {
        //Register 2
        RegisterResult regResult = service.register(new RegisterRequest("USEROUHOH", "pass", "email"));
        service.register(new RegisterRequest("BLAHBLAH", "passwojorij", "email"));

        //Create 3
        service.createGame("GAME!", regResult.getAuthToken());
        service.createGame("GAM*&*&", regResult.getAuthToken());
        service.createGame("PLAU!", regResult.getAuthToken());

        //show they were filled before clear
        Assertions.assertEquals(3, dataAccess.gamesSize());
        //only 2
        Assertions.assertEquals(2, dataAccess.userSize());
        Assertions.assertEquals(2, dataAccess.authSize());

        //Create 3 games
        service.clear();

        //all should be size 0
        Assertions.assertEquals(0, dataAccess.gamesSize());
        //0
        Assertions.assertEquals(0, dataAccess.userSize());
        //0
        Assertions.assertEquals(0, dataAccess.authSize());
    }

    @Test
    @Order(14)
    @DisplayName("Clear Users +")
    public void clearUsersTest() throws DataAccessException {
        //Create Users
        service.register(new RegisterRequest("user1", "pass1", "email"));
        service.register(new RegisterRequest("user2", "pass2", "email"));
        //2 users in the users table
        Assertions.assertEquals(2, dataAccess.userSize());

        //Clear users table
        dataAccess.clearUsers();
        // 0 rows
        Assertions.assertEquals(0, dataAccess.userSize());
    }

    @Test
    @Order(15)
    @DisplayName("Clear Auth +")
    public void clearAuthTest() throws DataAccessException {
        //Create Auths when register
        service.register(new RegisterRequest("user3", "pass3", "email"));
        service.register(new RegisterRequest("user4", "pass4", "email"));
        //2 users in the auth table
        Assertions.assertEquals(2, dataAccess.authSize());

        //Clear auth table
        dataAccess.clearAuths();
        // should have 0 rows
        Assertions.assertEquals(0, dataAccess.authSize());
    }

    @Test
    @Order(13)
    @DisplayName("Clear Games +")
    public void clearGamesTest() throws DataAccessException {
        //we need an auth
        RegisterResult regResult1 = service.register(new RegisterRequest("userrr1", "passs1", "email"));
        //Create 3
        service.createGame("game1", regResult1.getAuthToken());
        service.createGame("game2", regResult1.getAuthToken());
        service.createGame("game3", regResult1.getAuthToken());

        //3 games currently
        Assertions.assertEquals(3, dataAccess.gamesSize());

        //Clear the games
        dataAccess.clearGames();

        //no games in the table
        Assertions.assertEquals(0, dataAccess.gamesSize());
    }
}
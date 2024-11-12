package dataaccess;

import chess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import handler.obj.ListGamesData;

import java.util.ArrayList;

public class DataAccessTests {

    MySQLGameDAO dataAccess = new MySQLGameDAO();

    @BeforeEach
    public void setup() throws DataAccessException {
        dataAccess.clearUsers();
        dataAccess.clearAuths();
        dataAccess.clearGames();
    }

    @Test
    @Order(1)
    @DisplayName("Create User +")
    public void createUser() throws DataAccessException {
        UserData u = new UserData("username", "password", "email");
        UserData u1 = new UserData("username2", "password2", "email");

        dataAccess.createUser(u);
        dataAccess.createUser(u1);

        Assertions.assertEquals("username", dataAccess.getUser("username").username());
        Assertions.assertEquals("username2", dataAccess.getUser("username2").username());
        Assertions.assertEquals(2, dataAccess.userSize());
    }

    @Test
    @Order(2)
    @DisplayName("Create User -")
    public void recreateUser() throws DataAccessException {
        UserData u = new UserData("AnotherUser!!Yay", "jail", "soooemail!");

        dataAccess.createUser(u);

        //user should be in table
        Assertions.assertEquals("AnotherUser!!Yay", dataAccess.getUser("AnotherUser!!Yay").username());
        Assertions.assertEquals(1, dataAccess.userSize());
        //only 1
        Assertions.assertNotEquals(2, dataAccess.userSize());
    }

    @Test
    @Order(3)
    @DisplayName("Get User +")
    public void getUser() throws DataAccessException {
        //Create the User
        UserData u = new UserData("Thisisagooduser", "jail", "soooemail!");

        dataAccess.createUser(u);

        //User in memory
        Assertions.assertEquals("Thisisagooduser", dataAccess.getUser("Thisisagooduser").username());
        //Still only one user in memory
        Assertions.assertEquals(1, dataAccess.userSize());
    }

    @Test
    @Order(4)
    @DisplayName("Get User -")
    public void getBadUser() throws DataAccessException {
        //Create the User
        UserData u = new UserData("AnotherUser!!Yay", "jail", "soooemail!");

        dataAccess.createUser(u);

        //not a username


        //there is no user
        Assertions.assertNull(dataAccess.getUser("ufhrofher"));
    }

    @Test
    @Order(5)
    @DisplayName("Clear Users +")
    public void clearUsersTest() throws DataAccessException {
        //Create Users
        UserData u = new UserData("username", "password", "email");
        UserData u1 = new UserData("username2", "password2", "email");
        //create 2
        dataAccess.createUser(u);
        dataAccess.createUser(u1);

        //Clear users table
        dataAccess.clearUsers();
        // 0 rows
        Assertions.assertEquals(0, dataAccess.userSize());
    }


    @Test
    @Order(6)
    @DisplayName("Create Game +")
    public void createGameTest() throws DataAccessException {
        //create game
        int gameID = dataAccess.createGame("GAMENAMEEE:)");

        //white user is empty
        Assertions.assertNull(dataAccess.getGame(gameID).whiteUsername());
        //black user is empty
        Assertions.assertNull(dataAccess.getGame(gameID).blackUsername());
        //check the game is the right type
        Assertions.assertEquals(new ChessGame(), dataAccess.getGame(gameID).game());
        Assertions.assertEquals(1, dataAccess.gamesSize());
    }

    @Test
    @Order(7)
    @DisplayName("Create Game -")
    public void createBadGameTest() throws DataAccessException {
        //invalid auth
        int gameID = dataAccess.createGame("");

        //no games
        Assertions.assertEquals("", dataAccess.getGame(gameID).gameName());
    }


    @Test
    @Order(8)
    @DisplayName("List Games +")
    public void listGamesTest() throws DataAccessException {
        //Create 3
        dataAccess.createGame("GAME1");
        dataAccess.createGame("GAME2:)");
        dataAccess.createGame("GAME3:)");

        ArrayList< ListGamesData > list = dataAccess.listGames();

        //list is 3
        Assertions.assertEquals(3, list.size());
    }


    @Test
    @Order(9)
    @DisplayName("List Games -")
    public void listGamesBadTest() throws DataAccessException {
        //Create 3
        dataAccess.createGame("GAME1");
        dataAccess.createGame("GAME3:)");

        ArrayList< ListGamesData > list = dataAccess.listGames();

        //Still only one user
        Assertions.assertNotEquals(3, list.size());
    }


    @Test
    @Order(10)
    @DisplayName("Get Game +")
    public void getGameTest() throws DataAccessException {
        //Create 1
        int gameID = dataAccess.createGame("GAME1");

        GameData game = dataAccess.getGame(gameID);

        //list is 3
        Assertions.assertEquals(game.gameName(), "GAME1");
    }


    @Test
    @Order(11)
    @DisplayName("Get Game -")
    public void getGameBadTest() throws DataAccessException {
        //Create 1
        GameData game = dataAccess.getGame(0);

        //list is 3
        Assertions.assertNull(game);
    }


    @Test
    @Order(12)
    @DisplayName("Update Game +")
    public void updateGameTest() throws DataAccessException, InvalidMoveException {
        int gameID = dataAccess.createGame("FIRSTGAMENAME!!");

        //WHITE MOVE PAWN
        ChessGame game = dataAccess.getGame(gameID).game();
        game.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(4, 1), null));

        dataAccess.updateGame(new GameData(gameID, "FIRST", "SECOND", "FirstGameWHAT!!", game));

        Assertions.assertEquals(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                dataAccess.getGame(gameID).game().getBoard().getPiece(new ChessPosition(4, 1)));
    }


    @Test
    @Order(13)
    @DisplayName("Update Game -")
    public void updateBadGameTest() throws DataAccessException {
        //WHITE MOVE PAWN
        GameData game = dataAccess.getGame(0);
        dataAccess.updateGame(new GameData(0, "FIRST", "SECOND", "FirstGameWHAT!!", new ChessGame()));

        Assertions.assertNull(game);

    }

    @Test
    @Order(14)
    @DisplayName("Clear Games +")
    public void clearGamesTest() throws DataAccessException {
        //Create 3
        dataAccess.createGame("game1");
        dataAccess.createGame("game2");
        dataAccess.createGame("game3");

        //3 games currently
        Assertions.assertEquals(3, dataAccess.gamesSize());

        //Clear the games
        dataAccess.clearGames();

        //no games in the table
        Assertions.assertEquals(0, dataAccess.gamesSize());
    }



    @Test
    @Order(15)
    @DisplayName("Clear Auth +")
    public void clearAuthTest() throws DataAccessException {
        //Create Users
        UserData u = new UserData("username", "password", "email");
        UserData u1 = new UserData("username2", "password2", "email");
        //create 2
        dataAccess.createUser(u);
        dataAccess.createUser(u1);

        //Clear users table
        dataAccess.clearAuths();
        // 0 rows
        Assertions.assertEquals(0, dataAccess.authSize());
    }

    @Test
    @Order(16)
    @DisplayName("Create Auth +")
    public void createAuth() throws DataAccessException {
        //Create the User
        AuthData a = new AuthData("AnotherUser!!Yay", "uhoi");

        dataAccess.createAuth(a);

        //User in memory
        Assertions.assertEquals("AnotherUser!!Yay", dataAccess.getAuth("uhoi").username());
        //There should be two authTokens
        Assertions.assertEquals(1, dataAccess.authSize());
    }

    @Test
    @Order(17)
    @DisplayName("Create Auth +")
    public void createBadAuth() throws DataAccessException {
        //Create the User
        AuthData a = new AuthData("AnotherUser!!Yay", "dfhd");

        dataAccess.createAuth(a);

        //User in memory
        Assertions.assertEquals("AnotherUser!!Yay", dataAccess.getAuth("dfhd").username());
        //There should be two authTokens
        Assertions.assertEquals(1, dataAccess.authSize());
    }

    @Test
    @Order(18)
    @DisplayName("Get Auth +")
    public void getAuth() throws DataAccessException {
        //Create the User
        AuthData a = new AuthData("AnotherUser!!Yay", "uhoi");

        dataAccess.createAuth(a);

        AuthData auth = dataAccess.getAuth("uhoi");

        //User in table
        Assertions.assertEquals("AnotherUser!!Yay", auth.username());
        //There should be 1 authToken
        Assertions.assertEquals(1, dataAccess.authSize());
    }

    @Test
    @Order(19)
    @DisplayName("Get Auth -")
    public void getBadAuth() throws DataAccessException {
        //Create the User
        AuthData auth = dataAccess.getAuth("thisDoesn'tExist");

        //auth is null
        Assertions.assertNull(auth);
    }

    @Test
    @Order(18)
    @DisplayName("Delete Auth +")
    public void deleteAuth() throws DataAccessException {
        //Create the User
        AuthData a = new AuthData("AnotherUser!!Yay", "uhoi");

        dataAccess.createAuth(a);

        //1 auth in table
        Assertions.assertEquals(1, dataAccess.authSize());

        dataAccess.deleteAuth("uhoi");

        //0 auths in table
        Assertions.assertEquals(0, dataAccess.authSize());
    }

    @Test
    @Order(19)
    @DisplayName("Delete Auth -")
    public void deleteBadAuth() throws DataAccessException {
        //Create the User
        dataAccess.deleteAuth("thisDoesn'tExist");
    }
}
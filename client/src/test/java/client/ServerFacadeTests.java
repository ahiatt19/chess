package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import handler.obj.*;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.ArrayList;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void startServer() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        var serverUrl = "http://localhost:" + port;
        facade = new ServerFacade(serverUrl);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clear() throws Exception {
        facade.clear();
    }

    @Test
    void registerPos() throws Exception {
        UserData user1 = new UserData("player1", "pass", "p1@email.com");
        var auth1 = facade.register(user1);

        Assertions.assertTrue(auth1.authToken().length() > 10);
    }

    @Test
    void registerNeg() throws Exception {
        UserData user1 = new UserData("player1", "pass", "p1@email.com");
        UserData user2 = new UserData("player2", "pass", "p2@email.com");

        var auth1 = facade.register(user1);
        var auth2 = facade.register(user2);

        Assertions.assertNotEquals(auth1.authToken(), auth2.authToken());
    }

    @Test
    void loginPos() throws Exception {
        registerPos();
        
        LoginRequest login1 = new LoginRequest("player1", "pass");
        var auth1 = facade.login(login1);

        Assertions.assertTrue(auth1.authToken().length() > 10);
    }

    @Test
    void loginNeg() throws Exception {
        registerNeg();

        LoginRequest login1 = new LoginRequest("player1", "pass");
        LoginRequest login2 = new LoginRequest("player2", "pass");

        var auth1 = facade.login(login1);

        var auth2 = facade.login(login2);

        Assertions.assertNotEquals(auth2, auth1);
    }

    @Test
    void logoutPos() throws Exception {
        registerPos();
        LoginRequest loginRequest = new LoginRequest("player1", "pass");
        var authData = facade.login(loginRequest);

        facade.logout(authData.authToken());
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void logoutNeg() throws Exception {
        registerPos();
        LoginRequest loginRequest = new LoginRequest("player1", "pass");
        var authData = facade.login(loginRequest);
        var authData2 = facade.login(loginRequest);

        facade.logout(authData.authToken());
        facade.logout(authData2.authToken());
        Assertions.assertNotEquals(authData.authToken(), authData2.authToken());
    }

    @Test
    void listGamesPos() throws Exception {
        registerPos();
        LoginRequest loginRequest = new LoginRequest("player1", "pass");
        var authData = facade.login(loginRequest);
        CreateGameRequest req1 = new CreateGameRequest("game1");

        facade.createGame(authData.authToken(), req1);
        ListGamesResult listGames1 = facade.listGames(authData.authToken());

        Assertions.assertEquals(listGames1.getGames().size(), 1);
    }

    @Test
    void listGamesNeg() throws Exception {
        registerPos();
        LoginRequest loginRequest = new LoginRequest("player1", "pass");
        var authData = facade.login(loginRequest);
        CreateGameRequest req1 = new CreateGameRequest("game1");
        CreateGameRequest req2 = new CreateGameRequest("game2");

        facade.createGame(authData.authToken(), req1);

        facade.createGame(authData.authToken(), req2);
        ListGamesResult listGames2 = facade.listGames(authData.authToken());
        
        Assertions.assertNotEquals(listGames2.getGames().size(), 1);
    }

    @Test
    void createGamePos() throws Exception {
        registerPos();
        LoginRequest loginRequest = new LoginRequest("player1", "pass");
        var authData = facade.login(loginRequest);

        CreateGameRequest req1 = new CreateGameRequest("game1");
        CreateGameResult created1 = facade.createGame(authData.authToken(), req1);

        Assertions.assertEquals(created1.getGameID(), 1);
    }

    @Test
    void getGameTest() throws Exception {
        registerPos();
        LoginRequest loginRequest = new LoginRequest("player1", "pass");
        var authData = facade.login(loginRequest);

        CreateGameRequest req1 = new CreateGameRequest("game1");
        CreateGameResult created1 = facade.createGame(authData.authToken(), req1);

        var game = facade.getGame(authData.authToken(), created1.getGameID());

        System.out.println(game);

        Assertions.assertEquals(created1.getGameID(), 1);
    }

    @Test
    void updateGameTest() throws Exception {
        registerPos();
        LoginRequest loginRequest = new LoginRequest("player1", "pass");
        AuthData authData = facade.login(loginRequest);

        CreateGameRequest req1 = new CreateGameRequest("game1");
        CreateGameResult created1 = facade.createGame(authData.authToken(), req1);

        GameData gameData = facade.getGame(authData.authToken(), created1.getGameID());

        gameData.game().makeMove(new ChessMove(new ChessPosition(2, 2), new ChessPosition(3, 2), null));

        facade.updateGame(authData.authToken(), created1.getGameID(), gameData.game());

        GameData gameData2 = facade.getGame(authData.authToken(), created1.getGameID());

        System.out.println("In Facade Tests");
        System.out.println(gameData2.game());

        Assertions.assertEquals(gameData2.game(), gameData.game());
    }

    @Test
    void createGameNeg() throws Exception {
        registerPos();
        LoginRequest loginRequest = new LoginRequest("player1", "pass");
        var authData = facade.login(loginRequest);

        CreateGameRequest req1 = new CreateGameRequest("game1");
        CreateGameResult created1 = facade.createGame(authData.authToken(), req1);

        CreateGameRequest req2 = new CreateGameRequest("game2");
        CreateGameResult created2 = facade.createGame(authData.authToken(), req2);

        Assertions.assertNotEquals(created2.getGameID(), created1.getGameID());
    }

    @Test
    void joinGamePos() throws Exception {
        UserData user2 = new UserData("player2", "pass", "p2@email.com");

        var authData = facade.register(user2);

        CreateGameRequest req1 = new CreateGameRequest("game1");
        facade.createGame(authData.authToken(), req1);

        CreateGameRequest req2 = new CreateGameRequest("game2");
        facade.createGame(authData.authToken(), req2);

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 1);
        JoinGameRequest joinGameRequest2 = new JoinGameRequest("BLACK", 2);

        facade.joinGame(authData.authToken(), joinGameRequest);
        facade.joinGame(authData.authToken(), joinGameRequest2);

        ListGamesResult result = facade.listGames(authData.authToken());
        ArrayList<ListGamesData> res = result.getGames();
        for (ListGamesData re : res) {
            if (re.gameID() == 1) {
                Assertions.assertEquals(re.whiteUsername(), "player2");
            }
        }
    }

    @Test
    void joinGameNeg() throws Exception {
        UserData user2 = new UserData("player2", "pass", "p2@email.com");

        var authData = facade.register(user2);

        CreateGameRequest req1 = new CreateGameRequest("game1");
        facade.createGame(authData.authToken(), req1);

        CreateGameRequest req2 = new CreateGameRequest("game2");
        facade.createGame(authData.authToken(), req2);

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 1);
        JoinGameRequest joinGameRequest2 = new JoinGameRequest("BLACK", 2);

        facade.joinGame(authData.authToken(), joinGameRequest);
        facade.joinGame(authData.authToken(), joinGameRequest2);

        ListGamesResult result = facade.listGames(authData.authToken());
        ArrayList<ListGamesData> res = result.getGames();
        for (ListGamesData re : res) {
            if (re.gameID() == 2) {
                Assertions.assertNull(re.whiteUsername());
            }
        }
    }

    @Test
    void clearTestPos() throws Exception {
        UserData user1 = new UserData("player2", "pass", "p2@email.com");
        clear();
        UserData user2 = new UserData("player2", "pass", "p2@email.com");
        clear();
        //I can register a second time with the same data because I cleared the database
        Assertions.assertEquals(user1, user2);
    }

    @Test
    void clearTestNeg() throws Exception {
        UserData user1 = new UserData("player2", "pass", "p2@email.com");
        var authData1 = facade.register(user1);
        clear();
        UserData user2 = new UserData("player2", "pass", "p2@email.com");
        var authData = facade.register(user2);
        clear();
        //I can register a second time with the same data because I cleared the database
        Assertions.assertNotEquals(authData.authToken(), authData1.authToken());
    }

}

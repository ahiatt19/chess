package client;

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
    void register() throws Exception {
        UserData user1 = new UserData("player1", "pass", "p1@email.com");
        UserData user2 = new UserData("player2", "pass", "p2@email.com");

        var auth1 = facade.register(user1);

        Assertions.assertTrue(auth1.authToken().length() > 10);

        var auth2 = facade.register(user2);

        Assertions.assertNotEquals(auth1.authToken(), auth2.authToken());
    }

    @Test
    void login() throws Exception {
        register();
        LoginRequest login1 = new LoginRequest("player1", "pass");
        LoginRequest login2 = new LoginRequest("player2", "pass");

        var auth1 = facade.login(login1);

        Assertions.assertTrue(auth1.authToken().length() > 10);

        var auth2 = facade.login(login2);

        Assertions.assertNotEquals(auth2, auth1);
    }

    @Test
    void logout() throws Exception {
        register();
        LoginRequest loginRequest = new LoginRequest("player1", "pass");
        var authData = facade.login(loginRequest);
        var authData2 = facade.login(loginRequest);

        facade.logout(authData.authToken());
        Assertions.assertTrue(authData.authToken().length() > 10);

        facade.logout(authData2.authToken());
        Assertions.assertNotEquals(authData.authToken(), authData2.authToken());
    }

    @Test
    void listGames() throws Exception {
        register();
        LoginRequest loginRequest = new LoginRequest("player1", "pass");
        var authData = facade.login(loginRequest);
        CreateGameRequest req1 = new CreateGameRequest("game1");
        CreateGameRequest req2 = new CreateGameRequest("game2");

        facade.createGame(authData.authToken(), req1);
        ListGamesResult listGames1 = facade.listGames(authData.authToken());

        Assertions.assertEquals(listGames1.getGames().size(), 1);

        facade.createGame(authData.authToken(), req2);

        ListGamesResult listGames2 = facade.listGames(authData.authToken());
        Assertions.assertNotEquals(listGames2.getGames().size(), 1);
    }

    @Test
    void createGame() throws Exception {
        register();
        LoginRequest loginRequest = new LoginRequest("player1", "pass");
        var authData = facade.login(loginRequest);

        CreateGameRequest req1 = new CreateGameRequest("game1");
        CreateGameResult created1 = facade.createGame(authData.authToken(), req1);

        Assertions.assertEquals(created1.getGameID(), 1);

        CreateGameRequest req2 = new CreateGameRequest("game2");
        CreateGameResult created2 = facade.createGame(authData.authToken(), req2);

        Assertions.assertNotEquals(created2.getGameID(), created1.getGameID());
    }

    @Test
    void joinGame() throws Exception {
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
        for (int i = 0; i < res.size(); i++) {
            if (res.get(i).gameID() == 1 ) {
                Assertions.assertEquals(res.get(i).whiteUsername(),"player2");
            }
            else if (res.get(i).gameID() == 2 ) {
                Assertions.assertNull(res.get(i).whiteUsername());
            }
        }
    }

    @Test
    void clearTest() throws Exception {
        UserData user1 = new UserData("player2", "pass", "p2@email.com");
        var authData1 = facade.register(user1);
        clear();
        UserData user2 = new UserData("player2", "pass", "p2@email.com");
        var authData = facade.register(user2);
        clear();
        //I can register a second time with the same data because I cleared the database
        Assertions.assertEquals(user1, user2);
        Assertions.assertNotEquals(authData.authToken(), authData1.authToken());
    }

}

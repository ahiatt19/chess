package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


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
        UserData userData = new UserData("player3", "password", "p1@email.com");

        var authData = facade.register(userData);

        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void login() throws Exception {
        register();

        LoginRequest loginRequest = new LoginRequest("player3", "password");

        var authData = facade.login(loginRequest);

        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void logout() throws Exception {

        register();
        LoginRequest loginRequest = new LoginRequest("player3", "password");
        var authData = facade.login(loginRequest);

        facade.logout(authData.authToken());
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void listGames() throws Exception {
        register();
        LoginRequest loginRequest = new LoginRequest("player3", "password");
        var authData = facade.login(loginRequest);

        ListGamesResult listGames = facade.listGames(authData.authToken());
        System.out.println(listGames.getGames());
    }

    @Test
    void createGame() throws Exception {
        register();
        LoginRequest loginRequest = new LoginRequest("player3", "password");
        var authData = facade.login(loginRequest);

        CreateGameRequest req = new CreateGameRequest("gameName!");

        CreateGameResult createdGame = facade.createGame(authData.authToken(), req);
        System.out.println(createdGame.getGameID());
    }

}

package server;

import dataaccess.MemoryGameDAO;
import server.Clear.ClearHandler;
import server.Login.LoginHandler;
import server.Register.RegisterHandler;
import server.Logout.LogoutHandler;
import server.CreateGame.CreateGameHandler;
import server.ListGames.ListGamesHandler;
import server.JoinGame.JoinGameHandler;
import spark.*;
import service.UserService;

public class Server {

    MemoryGameDAO memory = new MemoryGameDAO();
    UserService service = new UserService(memory, memory, memory);
    RegisterHandler registerHandler = new RegisterHandler(service);
    LoginHandler loginHandler = new LoginHandler(service);
    LogoutHandler logoutHandler = new LogoutHandler(service);
    CreateGameHandler createGameHandler = new CreateGameHandler(service);
    ListGamesHandler listGamesHandler = new ListGamesHandler(service);
    JoinGameHandler joinGameHandler = new JoinGameHandler(service);
    ClearHandler clearHandler = new ClearHandler(service);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register a user
        Spark.post("/user", (req, res) -> registerHandler.handleRequest(req, res));
        //Log in a user
        Spark.post("/session", (req, res) -> loginHandler.handleRequest(req, res));
        //Logs out an authenticated user
        Spark.delete("/session", (req, res) -> logoutHandler.handleRequest(req, res));
        //Lists all the games
        Spark.get("/game", (req, res) -> listGamesHandler.handleRequest(req, res));
        //Create a new Chess Game
        Spark.post("/game", (req, res) -> createGameHandler.handleRequest(req, res));
        //Join a Chess Game
        Spark.put("/game", (req, res) -> joinGameHandler.handleRequest(req, res));
        //Clear ALL data from the database
        Spark.delete("/db", (req, res) -> clearHandler.handleRequest(req, res));

        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();

        //Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }



}

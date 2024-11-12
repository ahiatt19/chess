package server;

import dataaccess.MemoryGameDAO;
import dataaccess.MySQLGameDAO;
import server.clear.ClearHandler;
import server.login.LoginHandler;
import server.register.RegisterHandler;
import server.logout.LogoutHandler;
import server.creategame.CreateGameHandler;
import server.listgames.ListGamesHandler;
import server.joingame.JoinGameHandler;
import spark.*;
import service.Service;

public class Server {
    MySQLGameDAO database = new MySQLGameDAO();
    Service service = new Service(database);
    RegisterHandler registerHandler = new RegisterHandler(service);
    LoginHandler loginHandler = new LoginHandler(service);
    LogoutHandler logoutHandler = new LogoutHandler(service);
    CreateGameHandler createGameHandler = new CreateGameHandler(service);
    ListGamesHandler listGamesHandler = new ListGamesHandler(service);
    JoinGameHandler joinGameHandler = new JoinGameHandler(service);
    ClearHandler clearHandler = new ClearHandler(service);


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        System.out.println("RUN");

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
        Spark.delete("/db", (req, res) -> clearHandler.handleRequest(res));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }
}
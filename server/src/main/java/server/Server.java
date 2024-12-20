package server;

import dataaccess.MySQLGameDAO;
import server.websocket.WebSocketHandler;
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
    GetGameHandler getGameHandler = new GetGameHandler(service);
    UpdateGameHandler updateGameHandler = new UpdateGameHandler(service);
    ClearHandler clearHandler = new ClearHandler(service);
    WebSocketHandler webSocketHandler = new WebSocketHandler(service);


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);

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
        //Get a Chess Game in the DB
        Spark.get("/chess", (req, res) -> getGameHandler.handleRequest(req, res));
        //Update a Chess Game
        Spark.put("/update", (req, res) -> updateGameHandler.handleRequest(req, res));
        //Clear ALL data from the database
        Spark.delete("/db", (req, res) -> clearHandler.handleRequest(res));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }
}
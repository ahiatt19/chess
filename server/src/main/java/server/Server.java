package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import spark.*;
import service.UserService;
import dataaccess.MemoryGameDAO;

public class Server {

    MemoryGameDAO memory = new MemoryGameDAO();
    UserService service = new UserService(memory, memory);
    RegisterHandler registerHandler = new RegisterHandler(service);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register a user
        Spark.post("/user", (req, res) -> registerHandler.handleRequest(req, res));
        //Log in a user
        Spark.post("/session", (req, res) -> "Log in a user");
        //Logs out an authenticated user
        Spark.delete("/session", (req, res) -> "Logs out an authenticated user");
        //Lists all the games
        Spark.get("/game", (req, res) -> "Lists all the games");
        //Create a new Chess Game
        Spark.post("/game", (req, res) -> "Create a new Chess Game");
        //Join a Chess Game
        Spark.put("/game", (req, res) -> "Join a Chess Game");
        //Clear ALL data from the database
        Spark.delete("/db", (req, res) -> "Clear ALL data from the database");

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

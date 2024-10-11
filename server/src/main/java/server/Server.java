package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        endPoints();
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private static void endPoints() {
        Spark.post("/user", (req, res) -> "Register a user");
        Spark.post("/session", (req, res) -> "Log in a user");
        Spark.delete("/session", (req, res) -> "Logs out an authenticated user");
        Spark.get("/game", (req, res) -> "Lists all the games");
        Spark.post("/game", (req, res) -> "Create a new Chess Game");
        Spark.put("/game", (req, res) -> "Join a Chess Game");
        Spark.delete("/db", (req, res) -> "Clear ALL data from the database");
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

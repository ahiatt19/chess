package client;

import client.websocket.ServerMessageHandler;
import client.websocket.WebSocketFacade;
import handler.obj.*;
import model.UserData;
import server.ServerFacade;
import ui.ChessBoardUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static ui.ChessBoardUI.main;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final ServerMessageHandler handler;
    private State state = State.SIGNEDOUT;
    private String authToken;

    public ChessClient(String serverUrl, ServerMessageHandler handler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.handler = handler;
    }

    public String evaluateInput(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "play" -> play(params);
                case "observe" -> observe(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws Exception {
        if (params.length >= 3) {
            state = State.SIGNEDIN;
            UserData user = new UserData(params[0], params[1], params[2]);
            var auth = server.register(user);
            authToken = auth.authToken();
            return "Registered and Logged in as " + auth.username();
        }
        return "Please Register with this format: register <USERNAME> <PASSWORD> <EMAIL>";
    }

    public String login(String... params) throws Exception {
        if (params.length >= 2) {
            state = State.SIGNEDIN;
            LoginRequest req = new LoginRequest(params[0], params[1]);
            var auth = server.login(req);
            authToken = auth.authToken();
            return "Logged in as " + auth.username();
        }
        return "Please Login with this format: login <USERNAME> <PASSWORD>";
    }

    public String logout() throws Exception {
        assertSignedIn();
        state = State.SIGNEDOUT;
        server.logout(authToken);
        authToken = null;
        return "Logged out";
    }

    public String create(String... params) throws Exception {
        assertSignedIn();
        if (params.length >= 1) {
            CreateGameRequest request = new CreateGameRequest(params[0]);
            CreateGameResult result = server.createGame(authToken, request);
            ArrayList<ListGamesData> arr = server.listGames(authToken).getGames();
            int gameIndex = 0;
            for (int i = 0; i < arr.size(); i++) {
                if (Objects.equals(result.getGameID(), arr.get(i).gameID())) {
                    gameIndex = i + 1;
                }
            }
            return "Created new game: " + params[0] + ", Game ID: " + gameIndex;
        }
        return "Include your game name to create a game";
    }

    public String list() throws Exception {
        assertSignedIn();
        ArrayList<ListGamesData> res = server.listGames(authToken).getGames();
        String str = "If White/Black equals 'null', the spot is open to join.";
        for (int i = 0; i < res.size(); i++) {
            str = str + "\n" + (i + 1) + ") Game Name: " + res.get(i).gameName() +
                    "\n" + "   White: " + res.get(i).whiteUsername() +
                    "\n" + "   Black: " + res.get(i).blackUsername();
        }
        return str;
    }

    public String play(String... params) throws Exception {
        assertSignedIn();
        if (params.length >= 2) {
            ArrayList<ListGamesData> arr = server.listGames(authToken).getGames();
            int gameID = 0;
            for (int i = 0; i < arr.size(); i++) {
                if (Objects.equals(params[1], Integer.toString(i + 1))) {
                    gameID = arr.get(i).gameID();
                }
            }
            JoinGameRequest request = new JoinGameRequest(params[0].toUpperCase(), gameID);
            server.joinGame(authToken, request);
            main();
            var ws = new WebSocketFacade(serverUrl, handler);
            ws.joinGame(authToken, params[0].toUpperCase());
            return "Joined Game " + params[1] + " as " + params[0].toUpperCase();
        }
        return "Include WHITE/BLACK and a game ID in your request";
    }

    public String observe(String... params) throws Exception {
        assertSignedIn();
        if (params.length >= 1) {
            ListGamesResult res1 = server.listGames(authToken);
            ArrayList<ListGamesData> arr = res1.getGames();
            int gameID = 0;
            for (int i = 0; i < arr.size(); i++) {
                if (Objects.equals(params[0], Integer.toString(i + 1))) {
                    gameID = arr.get(i).gameID();
                }
            }
            main();
            return "Observing Game ID: " + params[0];
        }
        return "Include game ID";
    }

    public String help() {
        if (state == State.SIGNEDIN) {
            return """
                    logout -- Logout of Chess\
                    
                    create <GAME_NAME> -- Create a New Game\
                    
                    list -- List all the Games\
                    
                    play <COLOR> <GAME_ID> -- Play a Game\
                    
                    observe <GAME_ID> -- Watch a game\
                    
                    help -- Display Options""";
        } else {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> -- Register a new account\
                    
                    login <USERNAME> <PASSWORD> -- Login to account\
                    
                    quit -- Quit Chess\
                    
                    help -- Display Options""";
        }
    }

    private void assertSignedIn() throws Exception {
        if (state == State.SIGNEDOUT) {
            throw new Exception("You must sign in");
        }
    }

    //websocket stuff
    // public void notfiy()

}

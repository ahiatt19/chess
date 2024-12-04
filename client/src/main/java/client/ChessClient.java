package client;

import chess.*;
import client.websocket.ServerMessageHandler;
import client.websocket.WebSocketFacade;
import handler.obj.*;
import model.GameData;
import model.UserData;
import server.ServerFacade;
import ui.GamePlayUI;
import ui.UserType;

import java.util.*;

import static ui.ChessBoardUI.main;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final ServerMessageHandler handler;
    private State state = State.SIGNEDOUT;
    private boolean inGameplay = false;
    private String authToken;
    private GamePlayUI gamePlayUI = new GamePlayUI();
    private boolean resigning = false;

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
            if (resigning) {
                return switch (cmd.toUpperCase()) {
                    case "YES" -> actuallyResign();
                    default -> dontResign();
                };
            }
            return switch (cmd) {
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "play" -> play(params);
                case "observe" -> observe(params);
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "resign" -> resign();
                case "highlight" -> highlight(params);
                case "move" -> move(params);
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

            gamePlayUI.setVars(gameID, params[1], params[0].toUpperCase(), UserType.PLAYER);
            inGameplay = true;

            GameData gameData = server.getGame(authToken, gameID);
            ChessBoard chessBoard = gameData.game().getBoard();
            main(gamePlayUI.getPlayerColor(), chessBoard, null, null);
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
            inGameplay = true;
            gamePlayUI.setVars(gameID, params[0], "WHITE", UserType.OBSERVER);
            GameData gameData = server.getGame(authToken, gamePlayUI.getGameID());
            ChessBoard chessBoard = gameData.game().getBoard();
            main("WHITE", chessBoard, null, null);
            return "Observing Game ID: " + params[0];
        }
        return "Include game ID";
    }

    public String redraw() throws Exception {
        assertSignedIn();
        assertInGameplay();

        GameData gameData = server.getGame(authToken, gamePlayUI.getGameID());
        ChessBoard chessBoard = gameData.game().getBoard();
        main(gamePlayUI.getPlayerColor(), chessBoard, null, null);
        return "It's " + gameData.game().getTeamTurn() + "'s Turn to Move";
    }

    public String leave() throws Exception {
        assertSignedIn();
        assertInGameplay();
        inGameplay = false;
        String gameID = gamePlayUI.getUserGameID();
        gamePlayUI = new GamePlayUI();
        return "You have left game " + gameID;
    }

    public String resign() throws Exception {
        assertSignedIn();
        assertInGameplay();
        assertPlayer();

        resigning = true;
        return "Are you sure you want to resign? YES/NO";
    }

    public String actuallyResign() {
        // change the player to an observer so they can only look at the game now
        String gameID = gamePlayUI.getUserGameID();
        gamePlayUI.setVars(gamePlayUI.getGameID(), gamePlayUI.getUserGameID(), gamePlayUI.getPlayerColor(), UserType.OBSERVER);
        resigning = false;
        return "You Have Resigned From Game ID: " + gameID;
    }

    public String dontResign() {
        resigning = false;
        return "You Did Not Resign";
    }

    public String highlight(String... params) throws Exception {
        assertSignedIn();
        assertInGameplay();
        if (params.length >= 1) {
            String num = params[0].substring(0, 1);
            String let = params[0].substring(1, 2);
            String numRegex = "^[1-9]$";
            String letRegex = "^[a-hA-H]$";
            if (num.matches(numRegex) && let.matches(letRegex)) {
                int row = Integer.parseInt(num);
                int col = letterCoor(let.toUpperCase());
                ChessPosition pos = new ChessPosition(row, col);
                GameData gameData = server.getGame(authToken, gamePlayUI.getGameID());
                ChessGame chessGame = gameData.game();
                if (chessGame.getChessPiece(pos)) {
                    Collection<ChessMove> moves = chessGame.validMoves(pos);
                    main(gamePlayUI.getPlayerColor(), chessGame.getBoard(), moves, pos);

                    return "Possible Moves for " + params[0];
                } else {
                    return "There is not a Piece at " + params[0];
                }
            }
        }
        return "The Coordinates are Not Valid";
    }

    public String move(String... params) throws Exception {
        assertSignedIn();
        assertInGameplay();
        assertPlayer();
        if (params.length >= 2) {
            String numRegex = "^[1-9]$";
            String letRegex = "^[a-hA-H]$";
            String startNum = params[0].substring(0, 1);
            String startLet = params[0].substring(1, 2);
            String endNum = params[1].substring(0, 1);
            String endLet = params[1].substring(1, 2);
            if (startNum.matches(numRegex) && endNum.matches(numRegex) && startLet.matches(letRegex) && endLet.matches(letRegex)) {
                ChessPosition startPos = new ChessPosition(Integer.parseInt(startNum), letterCoor(startLet.toUpperCase()));
                ChessPosition endPos = new ChessPosition(Integer.parseInt(endNum), letterCoor(endLet.toUpperCase()));
                GameData gameData = server.getGame(authToken, gamePlayUI.getGameID());
                ChessGame chessGame = gameData.game();

                ChessBoard board = chessGame.getBoard();
                ChessPiece piece = board.getPiece(startPos);

                if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && Objects.equals(gamePlayUI.getPlayerColor(), "WHITE")) ||
                        (piece.getTeamColor() == ChessGame.TeamColor.BLACK && Objects.equals(gamePlayUI.getPlayerColor(), "BLACK"))) {
                    chessGame.makeMove(new ChessMove(startPos, endPos, null));
                    server.updateGame(authToken, gamePlayUI.getGameID(), chessGame);

                    main(gamePlayUI.getPlayerColor(), chessGame.getBoard(), null, null);

                    return gamePlayUI.getPlayerColor() + " made the move " + params[0] + " -> " + params[1];
                } else {
                    return "You May Only Move " + gamePlayUI.getPlayerColor() + " Pieces";
                }
            }
        }
        return "The Coordinates are Not Valid";
    }

    public int letterCoor(String letter) {
        return switch (letter) {
            case "A" -> 1;
            case "B" -> 2;
            case "C" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            case "F" -> 6;
            case "G" -> 7;
            case "H" -> 8;
            default -> throw new IllegalStateException("Unexpected coordinate: " + letter);
        };
    }

    public String help() {
        if (state == State.SIGNEDIN) {
            if (inGameplay) {
                if (gamePlayUI.getUserType() == UserType.OBSERVER) {
                    return """
                            highlight <1-8><a-b> -- Highlight Possible Moves for a Piece\
                            
                            redraw -- Redraws Chess Board\
                            
                            leave -- Leave the Game\
                            
                            help -- Display Options""";
                }
                if (gamePlayUI.getUserType() == UserType.PLAYER) {
                    return """
                            move <1-8><a-b> <1-8><a-b> -- Make a Move from Piece to New Coordinate\
                            
                            highlight <1-8><a-b> -- Highlight Possible Moves for a Piece\
                            
                            redraw -- Redraws Chess Board\
                            
                            leave -- Leave the Game\
                            
                            resign -- Forfeit the Game\
                            
                            help -- Display Options""";
                } else {
                    return "There is an error with your user type";
                }
            } else {
                return """
                    create <GAME_NAME> -- Create a New Game\
                    
                    list -- List all the Games\
                    
                    play <COLOR> <GAME_ID> -- Play a Game\
                    
                    observe <GAME_ID> -- Watch a Game\
                    
                    logout -- Logout of Chess\
                    
                    help -- Display Options""";
            }
        } else {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> -- Register a New Account\
                    
                    login <USERNAME> <PASSWORD> -- Login to Account\
                    
                    quit -- Quit Chess\
                    
                    help -- Display Options""";
        }
    }

    private void assertSignedIn() throws Exception {
        if (state == State.SIGNEDOUT) {
            throw new Exception("You must sign in");
        }
    }

    private void assertInGameplay() throws Exception {
        if (!inGameplay) {
            throw new Exception("You must join a game");
        }
    }

    private void assertPlayer() throws Exception {
        if (gamePlayUI.getUserType() != UserType.PLAYER) {
            throw new Exception("You must be a player to resign");
        }
    }
}

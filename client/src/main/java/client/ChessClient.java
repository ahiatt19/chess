package client;

import server.ServerFacade;
import com.google.gson.Gson;

import java.util.Arrays;

public class ChessClient {
    //private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    //private State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String evaluateInput(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws Exception {
        return "we registered ho";
    }

    public String help() {
        return "why are you asking for help UGH";
    }
}

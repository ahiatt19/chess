package client;

import model.UserData;
import server.ServerFacade;
import com.google.gson.Gson;

import java.util.Arrays;

public class ChessClient {
    //private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;

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
                case "quit" -> quit();
                case "register" -> register(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String quit() throws Exception {
        return "You have quit, thanks for playing!";
    }

    public String register(String... params) throws Exception {
        if (params.length >= 3) {
            //state = State.SIGNEDIN;
            UserData user = new UserData(params[0], params[1], params[2]);
            server.register(user);
            return "needs to transition to post login";
        }
        return "we registered ho";
    }

    public String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL>\
                
                login <USERNAME> <PASSWORD>\
                
                quit\
                
                help""";
    }
}

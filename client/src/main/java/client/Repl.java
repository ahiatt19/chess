package client;


import client.websocket.ServerMessageHandler;
import websocket.messages.NotficationMessage;
import websocket.messages.ServerMessage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;

import static java.awt.Color.RED;
import static ui.EscapeSequences.*;

public class Repl implements ServerMessageHandler {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println(PURPLE + "\u265A\u2655 Welcome to Aubry's Pink Chess Game. \u2655\u265A");
        System.out.print(BLUE + "Type 'help' to get started!");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        System.out.println();
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = client.evaluateInput(line);
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
            System.out.println();
        }
    }

    public void notify(String message) throws Exception {
        if (Objects.equals(message, "LOAD GAME")) {
            System.out.println();
            String str = client.redraw();
            System.out.println(BLUE + str);
            //Scanner scanner = new System(System.in);
            // String line = scanner.nextLine();
            printPrompt();
        } else {
            System.out.println();
            System.out.println(PURPLE + message);
            printPrompt();
        }
    }

    public void printPrompt() {
        System.out.print(RESET + ">>> ");
    }
}

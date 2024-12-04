package client;


import client.websocket.ServerMessageHandler;
import websocket.messages.NotficationMessage;
import websocket.messages.ServerMessage;

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

    public void notify(String message) {
        System.out.println(RED + "message.");
        printPrompt();
    }
/*
    public void notifyNotif(NotficationMessage message) {
        System.out.println(RED + message.);
        printPrompt();
    }*/

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> ");
    }
}

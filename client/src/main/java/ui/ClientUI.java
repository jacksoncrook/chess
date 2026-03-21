package ui;

import client.Client;
import client.PreloginClient;

import java.util.Scanner;

import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class ClientUI {
    private State state = State.PRELOGIN;
    private Client currentClient;
    private String serverUrl;

    public ClientUI(String serverUrl) {
        this.serverUrl = serverUrl;
        currentClient = new PreloginClient(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to Chess! Register or sign in to start");
        System.out.print(currentClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = currentClient.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void notify(String message) {
        System.out.println(message);
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + ">>> ");
    }
}

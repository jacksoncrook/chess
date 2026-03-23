package ui;

import client.Client;
import client.GameplayClient;
import client.PostloginClient;
import client.PreloginClient;
import model.AuthData;

import java.util.Scanner;

import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class ClientUI {
    private Client currentClient;
    private ClientResult.Type currentClientType;
    private final String serverUrl;

    public ClientUI(String serverUrl) {
        this.serverUrl = serverUrl;
        currentClient = new PreloginClient(serverUrl);
        currentClientType = currentClient.getType();
    }

    public void run() {
        System.out.println("Welcome to Chess! Register or sign in to start");
        System.out.print(currentClient.help().message());


        Scanner scanner = new Scanner(System.in);
        var result = new ClientResult(null, "", null);
        while (!result.message().equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = currentClient.eval(line);
                System.out.print(result.message());
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }

            if (result.type() != currentClientType) {
                AuthData authData = result.authData();
                if (result.type() != null) {
                    switch (result.type()) {
                        case PRELOGIN -> currentClient = new PreloginClient(serverUrl);
                        case POSTLOGIN -> currentClient = new PostloginClient(serverUrl, authData);
                        case GAMEPLAY -> currentClient = new GameplayClient(serverUrl, authData, result.gameID());
                    }
                }
                currentClientType = result.type();

                if (result.message() == null) {
                    result = new ClientResult(result.type(), "", result.authData());
                }
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + ">>> ");
    }
}

package client;

import java.util.Arrays;

import model.*;
import ui.ClientResult;
import ui.ClientResult.*;

import static ui.ClientResult.Type.*;

public class GameplayClient extends Client {
    private final ServerFacade server;
    private final AuthData authData;

    public GameplayClient(String serverUrl, AuthData authData) {
        server = new ServerFacade(serverUrl);
        this.authData = authData;
        type = GAMEPLAY;
    }

    public ClientResult eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "l", "logout" -> logout();
                case "h", "help" -> help();
                case "menu" -> menu();
                default -> unknownCommand();
            };
        } catch (Exception ex) {
            return new ClientResult(GAMEPLAY, ex.getMessage(), authData);
        }
    }

    public ClientResult logout() throws Exception {
        server.logout(new LogoutRequest(authData.authToken()));
        String message = "Successfully logged out";
        return new ClientResult(PRELOGIN, message, null);
    }

    public ClientResult menu() {
        String message = "Returned to menu";
        return new ClientResult(POSTLOGIN, message, authData);
    }

    public ClientResult help() {
        String helpMessage = """
                - help
                - redraw
                - logout
                - menu
                """;
        return new ClientResult(GAMEPLAY, helpMessage, authData);
    }
}
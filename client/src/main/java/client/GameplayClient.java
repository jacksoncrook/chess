package client;

import java.util.Arrays;

import model.AuthData;
import ui.ClientResult;
import ui.ClientResult.*;

import static ui.ClientResult.Type.*;

public class GameplayClient extends Client {
    private final ServerFacade server;
    private final Type type = GAMEPLAY;
    private AuthData authData;

    public GameplayClient(String serverUrl, AuthData authData) {
        server = new ServerFacade(serverUrl);
        this.authData = authData;
    }

    public ClientResult eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "l", "logout" -> logout();
                case "h", "help" -> help();
                default -> unknownCommand();
            };
        } catch (Exception ex) {
            return new ClientResult(GAMEPLAY, ex.getMessage(), authData);
        }
    }

    public ClientResult logout() throws Exception {
        String message = String.format("%s left the shop", "you");
        return new ClientResult(PRELOGIN, message, null);
    }


    public ClientResult help() {
        String helpMessage = """
                - help
                - redraw
                - logout
                - quit
                """;
        return new ClientResult(GAMEPLAY, helpMessage, authData);
    }
}
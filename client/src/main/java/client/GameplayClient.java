package client;

import java.util.Arrays;

import ui.ClientResult;
import ui.ClientResult.*;

import static ui.ClientResult.Type.*;

public class GameplayClient extends Client {
    private final ServerFacade server;
    private final Type type = GAMEPLAY;

    public GameplayClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public ClientResult eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "l", "logout" -> logout();
                case "q", "quit" -> new ClientResult(null, "quit");
                case "h", "help" -> help();
                default -> unknownCommand();
            };
        } catch (Exception ex) {
            return new ClientResult(GAMEPLAY, ex.getMessage());
        }
    }

    public ClientResult logout() throws Exception {
        String message = String.format("%s left the shop", "you");
        return new ClientResult(PRELOGIN, message);
    }


    public ClientResult help() {
        String helpMessage = """
                - help
                - redraw
                - logout
                - quit
                """;
        return new ClientResult(GAMEPLAY, helpMessage);
    }
}
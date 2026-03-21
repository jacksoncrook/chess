package client;

import java.util.Arrays;

public class GameplayClient implements Client {
    private final ServerFacade server;

    public GameplayClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "l", "logout" -> logout();
                case "q", "quit" -> "quit";
                case "h", "help" -> help();
                default -> unknownCommand();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String logout() throws Exception {
        return String.format("%s left the shop", "you");
    }


    public String help() {
        return """
                - help
                - redraw
                - logout
                - quit
                """;
    }

    public String unknownCommand() {
        return """
                Unknown Command:
               """ + help();
    }
}
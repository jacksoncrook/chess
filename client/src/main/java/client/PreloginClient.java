package client;

import java.util.Arrays;

import model.*;

public class PreloginClient implements Client {
    private String visitorName = null;
    private final ServerFacade server;

    public PreloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "l", "login" -> login(params);
                case "r", "register" -> register();
                case "q", "quit", "e", "x", "exit" -> "quit";
                case "h", "help" -> help();
                default -> unknownCommand();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws Exception {
        if (params.length >= 1) {
            visitorName = String.join("-", params);
            //ws.enterPetShop(visitorName);
            return String.format("You signed in as %s.", visitorName);
        }
        throw new Exception("Expected: <yourname>");
    }


    public String register() throws Exception {
        //ws.register(visitorName);
        return String.format("%s left the shop", visitorName);
    }


    public String help() {
        return """
            - login
            - register
            - help
            - quit
            """;
    }

    public String unknownCommand() {
        return """
                Unknown Command:
               """ + help();
    }
}
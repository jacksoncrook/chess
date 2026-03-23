package client;

import java.util.Arrays;

import ui.ClientResult;
import ui.ClientResult.Type;

import static ui.ClientResult.Type.*;

public class PreloginClient extends Client {
    private String visitorName = null;
    private final ServerFacade server;
    private final Type type = PRELOGIN;

    public PreloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public ClientResult eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "l", "login" -> login(params);
                case "r", "register" -> register();
                case "q", "quit", "e", "x", "exit" -> new ClientResult(null, "quit");
                case "h", "help" -> help();
                default -> unknownCommand();
            };
        } catch (Exception ex) {
            return new ClientResult(PRELOGIN, ex.getMessage());
        }
    }

    public ClientResult login(String... params) throws Exception {
        if (params.length >= 1) {
            visitorName = String.join("-", params);
            //ws.enterPetShop(visitorName);
            String message = String.format("You signed in as %s.", visitorName);
            return new ClientResult(POSTLOGIN, message);
        }
        throw new Exception("Expected: <yourname>");
    }


    public ClientResult register(String... params) throws Exception {
        if (params.length >= 1) {
            visitorName = String.join("-", params);
            //ws.enterPetShop(visitorName);
            String message = String.format("You registered as %s.", visitorName);
            return new ClientResult(POSTLOGIN, message);
        }
        throw new Exception("Expected: <yourname>");
    }


    public ClientResult help() {
        String helpMessage = """
            - login
            - register
            - help
            - quit
            """;
        return new ClientResult(GAMEPLAY, helpMessage);
    }
}
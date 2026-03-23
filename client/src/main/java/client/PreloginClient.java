package client;

import java.util.Arrays;

import model.*;

import ui.ClientResult;

import static ui.ClientResult.Type.*;

public class PreloginClient extends Client {
    private final ServerFacade server;

    public PreloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        type = PRELOGIN;
    }

    public ClientResult eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "l", "login" -> login(params);
                case "r", "register" -> register(params);
                case "q", "quit", "exit" -> new ClientResult(null, "quit", null);
                case "h", "help" -> help();
                default -> unknownCommand();
            };
        } catch (Exception ex) {
            return new ClientResult(PRELOGIN, ex.getMessage(), null);
        }
    }

    public ClientResult login(String... params) throws Exception {
        if (params.length == 2) {
            LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
            AuthData loginResult = server.login(loginRequest);
            String message = String.format("You signed in as %s.", loginResult.username());
            return new ClientResult(POSTLOGIN, message, loginResult);
        }
        throw new Exception("Expected: <username> <password>");
    }


    public ClientResult register(String... params) throws Exception {
        if (params.length == 3) {
            UserData registerRequest = new UserData(params[0], params[1], params[2]);
            AuthData registerResult = server.register(registerRequest);
            String message = String.format("You registered as %s.", registerResult.username());
            return new ClientResult(POSTLOGIN, message, registerResult);
        }
        throw new Exception("Expected: <username> <password> <email>");
    }


    public ClientResult help() {
        String helpMessage = """
            - login
            - register
            - help
            - quit""";
        return new ClientResult(PRELOGIN, helpMessage, null);
    }
}
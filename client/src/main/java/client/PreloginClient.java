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
                case "hiddencommandclear" -> clear();
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

    public ClientResult clear() throws Exception {
        server.clear();
        return new ClientResult(PRELOGIN, "Successfully emptied database", null);
    }

    public ClientResult help() {
        String helpMessage = """
            - help                                          Display this menu
            - login <username> <password>                   Login as user <username>
            - register <username> <password> <email>        Register new user <username>
            - quit                                          Exit the program""";
        return new ClientResult(PRELOGIN, helpMessage, null);
    }
}
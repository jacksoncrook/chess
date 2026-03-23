package client;

import com.google.gson.Gson;
import model.*;

import ui.ClientResult;

import java.util.Arrays;

import ui.ClientResult.Type;

import static ui.ClientResult.Type.*;

public class PostloginClient extends Client {
    private final ServerFacade server;
    private final Type type = POSTLOGIN;
    private final AuthData authData;

    public PostloginClient(String serverUrl, AuthData authData) {
        server = new ServerFacade(serverUrl);
        this.authData = authData;
    }

    public ClientResult eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "list" -> listGames();
                case "logout" -> logout();
                case "join" -> joinGame(params);
                case "h", "help" -> help();
                default -> unknownCommand();
            };
        } catch (Exception ex) {
            return new ClientResult(POSTLOGIN, ex.getMessage(), authData);
        }
    }


    public ClientResult listGames() throws Exception {
        GetGamesResult gameList = server.listGames(new GetGamesRequest(authData.authToken()));
        var result = new StringBuilder();
        var gson = new Gson();
        for (GameData gameData : gameList.games()) {
            result.append(gson.toJson(gameData)).append('\n');
        }
        return new ClientResult(POSTLOGIN, result.toString(), authData);
    }

    public ClientResult joinGame(String... params) throws Exception {
        if (params.length == 1) {
            int id = Integer.parseInt(params[0]);
            String message = String.format("%s is %d", "cat", id);
            return new ClientResult(GAMEPLAY, message, authData);
        }
        throw new Exception("Expected: <pet id>");
    }


    public ClientResult logout() throws Exception {
        String message = String.format("%s left the shop", "you");
        return new ClientResult(PRELOGIN, message, null);
    }

    public ClientResult help() {
        String helpMessage = """
                - help
                - list
                - create
                - join
                - observe
                - logout
                - quit
                """;
        return new ClientResult(POSTLOGIN, helpMessage, authData);
    }
}
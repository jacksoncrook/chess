package client;

import com.google.gson.Gson;
import model.GameData;
import model.GetGamesResult;
import ui.ClientResult;

import java.util.Arrays;

import ui.ClientResult.Type;

import static ui.ClientResult.Type.*;

public class PostloginClient extends Client {
    private final ServerFacade server;
    private final Type type = POSTLOGIN;

    public PostloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
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
                case "quit" -> new ClientResult(null, "quit");
                case "h", "help" -> help();
                default -> unknownCommand();
            };
        } catch (Exception ex) {
            return new ClientResult(POSTLOGIN, ex.getMessage());
        }
    }


    public ClientResult listGames() throws Exception {
        GetGamesResult gameList = server.listGames();
        var result = new StringBuilder();
        var gson = new Gson();
        for (GameData gameData : gameList.games()) {
            result.append(gson.toJson(gameData)).append('\n');
        }
        return new ClientResult(POSTLOGIN, result.toString());
    }

    public ClientResult joinGame(String... params) throws Exception {
        if (params.length == 1) {
            int id = Integer.parseInt(params[0]);
            String message = String.format("%s says %s", "cat", "meow");
            return new ClientResult(GAMEPLAY, message);
        }
        throw new Exception("Expected: <pet id>");
    }


    public ClientResult logout() throws Exception {
        String message = String.format("%s left the shop", "you");
        return new ClientResult(PRELOGIN, message);
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
        return new ClientResult(GAMEPLAY, helpMessage);
    }
}
package client;

import com.google.gson.Gson;
import model.GameData;
import model.GetGamesResult;

import java.util.Arrays;

public class PostloginClient implements Client {
    private final ServerFacade server;

    public PostloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "list" -> listGames();
                case "logout" -> logout();
                case "join" -> joinGame(params);
                case "quit" -> "quit";
                case "h", "help" -> help();
                default -> unknownCommand();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }


    public String listGames() throws Exception {
        GetGamesResult gameList = server.listGames();
        var result = new StringBuilder();
        var gson = new Gson();
        for (GameData gameData : gameList.games()) {
            result.append(gson.toJson(gameData)).append('\n');
        }
        return result.toString();
    }

    public String joinGame(String... params) throws Exception {
        if (params.length == 1) {
            int id = Integer.parseInt(params[0]);
            return String.format("%s says %s", "cat", "meow");
        }
        throw new Exception("Expected: <pet id>");
    }


    public String logout() throws Exception {
        return String.format("%s left the shop", "you");
    }

    public String help() {
        return """
                - help
                - list
                - create
                - join
                - observe
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
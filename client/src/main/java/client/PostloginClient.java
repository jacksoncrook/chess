package client;

import model.*;

import ui.ClientResult;

import java.util.Arrays;

import static ui.ClientResult.Type.*;

public class PostloginClient extends Client {
    private final ServerFacade server;
    private final AuthData authData;

    public PostloginClient(String serverUrl, AuthData authData) {
        server = new ServerFacade(serverUrl);
        this.authData = authData;
        type = POSTLOGIN;
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
                case "c", "create" -> createGame(params);
                case "h", "help" -> help();
                default -> unknownCommand();
            };
        } catch (Exception ex) {
            return new ClientResult(POSTLOGIN, ex.getMessage(), authData);
        }
    }

    public ClientResult listGames() throws Exception {
        gameList = server.listGames(new GetGamesRequest(authData.authToken()));
        var result = new StringBuilder();
        int i = 1;
        for (GameData gameData : gameList.games()) {
            result.append(i).append(". ").append(gameData.gameName())
                    .append("\n\t").append("White: ").append(gameData.whiteUsername())
                    .append("\n\t").append("Black: ").append(gameData.blackUsername()).append("\n\n");
            i++;
        }

        result.delete(result.length() - 2, result.length());
        return new ClientResult(POSTLOGIN, result.toString(), authData);
    }

    public ClientResult createGame(String... params) throws Exception {
        if (params.length == 1) {
            CreateGameRequest request = new CreateGameRequest(authData.authToken(), params[0]);
            CreateGameResult createGameResult = server.createGame(request);
            String gameID = String.valueOf(createGameResult.gameID());
            String message = String.format("New game id is %s", gameID);
            return new ClientResult(POSTLOGIN, message, authData);
        }
        throw new Exception("Expected: <new game name>");
    }

    public ClientResult joinGame(String... params) throws Exception {
        gameList = server.listGames(new GetGamesRequest(authData.authToken()));
        if (params.length == 2) {
            int id = Integer.parseInt(params[0]);
            GameData gameData = gameList.get(id - 1);
            int joinGameID = gameData.gameID();
            JoinGameRequest request = new JoinGameRequest(params[1], joinGameID, authData.authToken());
            server.joinGame(request);
            String message = String.format("Successfully joined game %d: %s", id, gameData.gameName());
            return new ClientResult(GAMEPLAY, message, authData, String.valueOf(joinGameID));
        }
        throw new Exception("Expected: <game id> <black/white>");
    }


    public ClientResult logout() throws Exception {
        server.logout(new LogoutRequest(authData.authToken()));
        String message = String.format("%s successfully logged out", "you");
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
                """;
        return new ClientResult(POSTLOGIN, helpMessage, authData);
    }
}
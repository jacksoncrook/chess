package client;

import java.util.Arrays;

import model.*;
import ui.ClientResult;
import ui.ClientResult.*;

import static ui.ClientResult.Type.*;

public class GameplayClient extends Client {
    private final ServerFacade server;
    private final AuthData authData;
    private final int currentGameID;
    private final String currentGameIDString;

    public GameplayClient(String serverUrl, AuthData authData, String gameID) {
        server = new ServerFacade(serverUrl);
        this.authData = authData;
        this.currentGameIDString = gameID;
        this.currentGameID = Integer.parseInt(gameID);
        type = GAMEPLAY;
    }

    public ClientResult eval(String input) {
        if (currentGameIDString == null) {
            return new ClientResult(POSTLOGIN, "Error: invalid Game ID", authData);
        }

        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "l", "logout" -> logout();
                case "h", "help" -> help();
                case "r", "ref", "refresh", "redraw" -> redraw();
                case "menu" -> menu();
                default -> unknownCommand();
            };
        } catch (Exception ex) {
            return new ClientResult(GAMEPLAY, ex.getMessage(), authData, currentGameIDString);
        }
    }

    public ClientResult logout() throws Exception {
        server.logout(new LogoutRequest(authData.authToken()));
        String message = "Successfully logged out";
        return new ClientResult(PRELOGIN, message, null);
    }

    public ClientResult menu() {
        String message = "Returned to menu";
        return new ClientResult(POSTLOGIN, message, authData);
    }

    public ClientResult redraw() throws Exception {
        GetGamesResult gameList = server.listGames(new GetGamesRequest(authData.authToken()));
        var result = new StringBuilder();
        GameData currentGame = null;
        for (GameData gameData : gameList.games()) {
            if (gameData.gameID() == currentGameID) {
                currentGame = gameData;
                break;
            }
        }

        if (currentGame == null) {
            throw new RequestException("Error: game not found");
        }

        result.append(currentGame);
        return new ClientResult(GAMEPLAY, result.toString(), authData, currentGameIDString);
    }

    public ClientResult help() {
        String helpMessage = """
                - help
                - redraw
                - logout
                - menu
                """;
        return new ClientResult(GAMEPLAY, helpMessage, authData);
    }
}
package client;

import java.util.Arrays;

import model.*;
import ui.ClientResult;
import ui.ClientResult.*;

import static ui.ClientResult.Type.*;
import static ui.EscapeSequences.*;

public class GameplayClient extends Client {
    private final ServerFacade server;
    private final AuthData authData;
    private final int currentGameID;
    private final String currentGameIDString;
    private final String currentTeam;

    public GameplayClient(String serverUrl, AuthData authData, String gameID, String teamColor) {
        server = new ServerFacade(serverUrl);
        this.authData = authData;
        this.currentGameIDString = gameID;
        this.currentGameID = Integer.parseInt(gameID);
        this.currentTeam = teamColor;
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
                case "m", "move" -> makeMove(params);
                case "logout" -> logout();
                case "h", "help" -> help();
                case "r", "ref", "refresh", "redraw" -> redraw();
                case "menu" -> menu();
                default -> unknownCommand();
            };
        } catch (Exception ex) {
            return new ClientResult(GAMEPLAY, ex.getMessage(), authData, currentGameIDString, currentTeam);
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

        var result = printBoard(currentGame);
        return new ClientResult(GAMEPLAY, result, authData, currentGameIDString, currentTeam);
    }

    public ClientResult makeMove(String... params) {
        String message = "Movement has not been implemented: " + params[0];
        return new ClientResult(GAMEPLAY, message, authData, currentGameIDString, currentTeam);
    }

    public ClientResult help() {
        String helpMessage = """
                - help
                - redraw
                - logout
                - menu""";
        return new ClientResult(GAMEPLAY, helpMessage, authData, currentGameIDString, currentTeam);
    }

    public String printBoard(GameData gameData) {
        return ERASE_SCREEN + SET_BG_COLOR_DARK_GREY + SET_TEXT_BOLD + gameData.gameName() + ": " + gameData.whiteUsername() + ", " + gameData.blackUsername() + "\n"
                + gameData.game().getBoard();
    }
}
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
                case "l", "list" -> listGames();
                case "logout" -> logout();
                case "j", "join", "r", "rejoin" -> joinGame(params);
                case "o", "obs", "observe" -> observeGame(params);
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
                    .append("\n\t").append("White: ");
            if (gameData.whiteUsername() != null) {
                result.append(gameData.whiteUsername());
            }

            result.append("\n\t").append("Black: ");
            if (gameData.blackUsername() != null) {
                result.append(gameData.blackUsername());
            }
            result.append("\n\n");

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
        if (params.length == 2 && isInt(params[0])) {
            int id = Integer.parseInt(params[0]);
            GameData gameData = gameList.get(id - 1);
            int joinGameID = gameData.gameID();

            boolean rejoining = false;

            String teamColor = params[1];
            if (teamColor.equals("w") || teamColor.equals("white")) {
                teamColor = "WHITE";
                rejoining = (gameData.whiteUsername() != null && gameData.whiteUsername().equals(authData.username()));

            } else if (teamColor.equals("b") || teamColor.equals("black")) {
                teamColor = "BLACK";
                rejoining = (gameData.blackUsername() != null && gameData.blackUsername().equals(authData.username()));
            }

            if (rejoining) {
                String message = String.format("Successfully rejoined game %d: %s\n", id, gameData.gameName());
                return new ClientResult(GAMEPLAY, message, authData, String.valueOf(joinGameID), teamColor);
            }

            JoinGameRequest request = new JoinGameRequest(teamColor, joinGameID, authData.authToken());
            server.joinGame(request);
            String message = String.format("Successfully joined game %d: %s\n", id, gameData.gameName());
            return new ClientResult(GAMEPLAY, message, authData, String.valueOf(joinGameID), teamColor);
        }
        throw new Exception("Expected: <game id> <black/white>");
    }

    public ClientResult observeGame(String... params) throws Exception {
        gameList = server.listGames(new GetGamesRequest(authData.authToken()));
        if (params.length == 1 && isInt(params[0])) {
            int id = Integer.parseInt(params[0]);
            GameData gameData = gameList.get(id - 1);
            int joinGameID = gameData.gameID();

            String message = String.format("Now observing game %d: %s", id, gameData.gameName());
            return new ClientResult(GAMEPLAY, message, authData, String.valueOf(joinGameID), "WHITE");
        }
        throw new Exception("Expected: <game id>");
    }


    public ClientResult logout() throws Exception {
        server.logout(new LogoutRequest(authData.authToken()));
        String message = String.format("%s successfully logged out", authData.username());
        return new ClientResult(PRELOGIN, message, null);
    }

    public ClientResult help() {
        String helpMessage = """
                - help
                - list
                - create <name>
                - join <game id> <white/black>
                - observe <game id>
                - logout""";
        return new ClientResult(POSTLOGIN, helpMessage, authData);
    }

    private boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
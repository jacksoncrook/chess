package client;

import java.util.Arrays;
import java.util.List;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.*;
import ui.ClientResult;

import static chess.ChessGame.TeamColor.*;
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

        var result = outputGame(currentGame);
        return new ClientResult(GAMEPLAY, result, authData, currentGameIDString, currentTeam);
    }

    public ClientResult makeMove(String... params) {
        String message = "Movement has not been implemented \n";
        if (params.length == 2) {
            message += params[0] + " " + params[1];
        } else {
            message += "Expected: <start position> <end position>";
        }
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

    public String outputGame(GameData gameData) {
        return ERASE_SCREEN + SET_TEXT_BOLD +
                gameData.gameName() + ": " +
                gameData.whiteUsername() + ", " +
                gameData.blackUsername() + '\n' +
                printBoard(gameData.game()) +
                RESET_TEXT_BOLD_FAINT;
    }

    public String printBoard(ChessGame game) {
        int direction;
        int startingPoint;
        var result = new StringBuilder();

        if ("BLACK".equals(currentTeam)) {
            direction = -1;
            startingPoint = 9;
        } else {
            direction = 1;
            startingPoint = 0;
        }

        List<String> columnLabels = Arrays.asList("   ", " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", "   ");

        for (int row = 9 - startingPoint; 0 <= row && row <= 9; row -= direction) {
            for (int col = startingPoint; 0 <= col && col <= 9; col += direction) {
                if (row == 0 || row == 9) {
                    result.append(SET_BG_COLOR_LIGHT_GREY).append(SET_TEXT_COLOR_BLACK).append(columnLabels.get(col));
                } else if (col == 0 || col == 9) {
                    result.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(row).append(" ");
                } else {
                    ChessPiece piece = game.getBoard().getPiece(new ChessPosition(row, col));

                    if ((row + col) % 2 == 1) {
                        result.append(SET_BG_COLOR_WHITE);
                    } else {
                        result.append(SET_BG_COLOR_BROWN);
                    }

                    if (piece == null) {
                        result.append(EMPTY);
                        continue;
                    }

                    if (piece.getTeamColor() == WHITE) {
                        switch (piece.getPieceType()) {
                            case PAWN -> result.append(WHITE_PAWN);
                            case KNIGHT -> result.append(WHITE_KNIGHT);
                            case BISHOP -> result.append(WHITE_BISHOP);
                            case ROOK -> result.append(WHITE_ROOK);
                            case KING -> result.append(WHITE_KING);
                            case QUEEN -> result.append(WHITE_QUEEN);
                        }
                    } else if (piece.getTeamColor() == BLACK) {
                        switch (piece.getPieceType()) {
                            case PAWN -> result.append(BLACK_PAWN);
                            case KNIGHT -> result.append(BLACK_KNIGHT);
                            case BISHOP -> result.append(BLACK_BISHOP);
                            case ROOK -> result.append(BLACK_ROOK);
                            case KING -> result.append(BLACK_KING);
                            case QUEEN -> result.append(BLACK_QUEEN);
                        }
                    }
                }
            }

            result.append(RESET_BG_COLOR).append('\n');
        }

        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }
}
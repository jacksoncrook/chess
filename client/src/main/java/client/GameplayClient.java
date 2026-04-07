package client;

import java.util.*;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import model.*;
import ui.ClientResult;

import jakarta.websocket.Session;
import jakarta.websocket.MessageHandler;
import websocket.messages.LoadGameMessage;

import static chess.ChessGame.TeamColor.*;
import static ui.ClientResult.Type.*;
import static ui.EscapeSequences.*;

public class GameplayClient extends Client {
    private final ServerFacade server;
    private final AuthData authData;
    private final int currentGameID;
    private final String currentGameIDString;
    private final String currentTeam;
    private final Session session;

    public GameplayClient(String serverUrl, String serverUri, AuthData authData, String gameID, String teamColor) throws Exception {
        server = new ServerFacade(serverUrl, serverUri);
        this.authData = authData;
        this.currentGameIDString = gameID;
        this.currentGameID = Integer.parseInt(gameID);
        this.currentTeam = teamColor;
        type = GAMEPLAY;

        session = server.createSession();
        server.websocketConnect(session, this.authData.authToken(), currentGameID);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                try {
                    ChessGame game = new Gson().fromJson(message, LoadGameMessage.class).game();
                    try {
                        System.out.println(ERASE_LINE + "\n" + redraw(null, game).message());
                        System.out.print(RESET_BG_COLOR + RESET_TEXT_COLOR + ">>>");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }

                } catch (JsonSyntaxException ignore) {
                    System.out.println(ERASE_LINE + "\n" + message);
                    System.out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + ">>> ");
                }
            }
        });
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
                case "resign" -> resign();
                case "h", "help" -> help();
                case "r", "ref", "refresh", "redraw" -> redraw(null, null);
                case "lm", "lms", "legalms", "legalmoves", "lmoves" -> legalMoves(params);
                case "menu" -> menu();
                default -> unknownCommand();
            };
        } catch (Exception ex) {
            return new ClientResult(GAMEPLAY, ex.getMessage(), authData, currentGameIDString, currentTeam);
        }
    }

    public ClientResult logout() {
        String message = "Successfully logged out";
        try {
            menu();
            server.logout(new LogoutRequest(authData.authToken()));
        } catch (Exception e) {
            message = "Server error: returning to login menu";
        }
        return new ClientResult(PRELOGIN, message, null);
    }

    public ClientResult resign() {
        String message = "Game resigned";

        if (currentTeam == null) {
            message = "Observers cannot resign";
            return new ClientResult(GAMEPLAY, message, authData, currentGameIDString, null);
        }


        try {
            ChessGame game = getGame().game();
            if (game.getTeamTurn() == GAME_OVER) {
                return new ClientResult(GAMEPLAY, "Error: game already over", authData, currentGameIDString, currentTeam);
            }

            server.resign(session, authData.authToken(), currentGameID, currentTeam);
        } catch (Exception e) {
            message = "Internal server error";
        }
        return new ClientResult(GAMEPLAY, message, authData, currentGameIDString, currentTeam);
    }

    public ClientResult menu() {
        String message = ERASE_SCREEN + "Returned to menu";
        try {
            server.websocketLeave(session, authData.authToken(), currentGameID);
        } catch (Exception e) {
            message = "Server error: returning to menu";
        }
        return new ClientResult(POSTLOGIN, message, authData);
    }

    public ClientResult legalMoves(String... params) throws Exception {
        if (params.length != 1) {
            throw new RequestException("Expected: <position>");
        }

        String positionString = params[0];
        positionString = positionString.toLowerCase();
        if (!positionString.matches("[a-h][1-8]")) {
            throw new RequestException("Invalid position; Expected: <position>");
        }

        ChessPosition position = positionHandler(positionString);
        return redraw(position, null);
    }

    public ClientResult redraw(ChessPosition position, ChessGame game) throws Exception {
        GameData currentGame = getGame();
        if (game != null) {
            currentGame = currentGame.updateGameData(game);
        }

        var result = outputGame(currentGame, position);
        return new ClientResult(GAMEPLAY, result, authData, currentGameIDString, currentTeam);
    }

    public ClientResult makeMove(String... params) throws Exception {
        String message = "";

        if (currentTeam == null) {
            message = "Observers cannot make moves";
            return new ClientResult(GAMEPLAY, message, authData, currentGameIDString, null);
        }

        ChessGame currentGame = getGame().game();

        if (currentGame.getTeamTurn() == GAME_OVER) {
            message = "Error: can't move after game end";
            return new ClientResult(GAMEPLAY, message, authData, currentGameIDString, null);
        }

        if (!currentTeam.equals(currentGame.getTeamTurn().toString())) {
            message = "Error: not your turn";
            return new ClientResult(GAMEPLAY, message, authData, currentGameIDString, null);
        }

        if (params.length == 2 && params[0].matches("[a-h][1-8]") && params[1].matches("[a-h][1-8]")) {
            ChessMove move = new ChessMove(positionHandler(params[0]), positionHandler(params[1]), null);
            ChessPiece piece = currentGame.getBoard().getPiece(move.getStartPosition());

            boolean blackPawnPromotion = piece.getTeamColor() == BLACK && move.getEndPosition().getRow() == 1;
            boolean whitePawnPromotion = piece.getTeamColor() == WHITE && move.getEndPosition().getRow() == 8;

            if (blackPawnPromotion || whitePawnPromotion) {
                ChessPiece.PieceType promotionPiece = null;
                System.out.print("What would you like to promote to?");

                while (promotionPiece == null) {
                    String line = getLine();
                    if (line.equalsIgnoreCase("queen")) {
                        promotionPiece = ChessPiece.PieceType.QUEEN;
                    } else if (line.equalsIgnoreCase("rook")) {
                        promotionPiece = ChessPiece.PieceType.ROOK;
                    } else if (line.equalsIgnoreCase("bishop")) {
                        promotionPiece = ChessPiece.PieceType.BISHOP;
                    } else if (line.equalsIgnoreCase("knight")) {
                        promotionPiece = ChessPiece.PieceType.KNIGHT;
                    } else {
                        System.out.print("Error: invalid selection\nPlease choose Queen, Rook, Knight, or Bishop");
                    }
                }
                move = new ChessMove(move.getStartPosition(), move.getEndPosition(), promotionPiece);
            }

            server.makeMove(session, authData.authToken(), currentGameID, move);
        } else {
            message += "Expected: <start position> <end position>";
        }
        return new ClientResult(GAMEPLAY, message, authData, currentGameIDString, currentTeam);
    }

    private static String getLine() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + ">>> ");
        return scanner.nextLine();
    }

    private GameData getGame() throws Exception {
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

        return currentGame;
    }

    public ClientResult help() {
        String helpMessage;
        if (currentTeam == null) {
            helpMessage = """
                - help                                      Display this menu
                - redraw                                    Redraw the board locally
                - legalmoves <position>                     Display legal moves
                    - lmoves <position>
                - logout                                    Return to the login menu
                - menu                                      Return to the game selection menu""";
        } else {
            helpMessage = """
                - help                                      Display this menu
                - redraw                                    Redraw the board locally
                - legalmoves <position>                     Display legal moves
                    - lmoves <position>
                - move <start position> <end position>      Make a move
                - resign                                    Forfeit and end the game
                - logout                                    Return to the login menu
                - menu                                      Return to the game selection menu""";
        }
        return new ClientResult(GAMEPLAY, helpMessage, authData, currentGameIDString, currentTeam);
    }

    public String outputGame(GameData gameData, ChessPosition position) {
        return ERASE_SCREEN + SET_TEXT_BOLD +
                gameData.gameName() + ": " +
                gameData.whiteUsername() + ", " +
                gameData.blackUsername() + '\n' +
                printBoard(gameData.game(), position) +
                RESET_TEXT_BOLD_FAINT;
    }

    private enum SquareType {
        LIGHT,
        DARK,
        LIGHT_SELECTED,
        DARK_SELECTED,
        LIGHT_HIGHLIGHTED,
        DARK_HIGHLIGHTED,
    }


    public String printBoard(ChessGame game, ChessPosition position) {
        int direction;
        int startingPoint;
        var result = new StringBuilder();
        boolean drawMovement = position != null;
        Collection<ChessPosition> validMoveLocations = new ArrayList<>();

        if (drawMovement) {
            validMoveLocations = game.validMoveLocations(position);
        }

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
                    continue;

                } else if (col == 0 || col == 9) {
                    result.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(row).append(" ");
                    continue;
                }

                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece piece = game.getBoard().getPiece(currentPosition);
                SquareType square;

                if ((row + col) % 2 == 1) {
                    square = SquareType.LIGHT;
                } else {
                    square = SquareType.DARK;
                }

                if (validMoveLocations.contains(currentPosition)) { // Set square to highlighted if it's a valid move location
                    square = SquareType.values()[square.ordinal() + 4];

                } else if (currentPosition.equals(position)) { // Set square to selected if it's the queried position
                    square = SquareType.values()[square.ordinal() + 2];
                }

                switch (square) {
                    case LIGHT -> result.append(SET_BG_COLOR_WHITE);
                    case DARK -> result.append(SET_BG_COLOR_BROWN);
                    case LIGHT_SELECTED -> result.append(SET_BG_COLOR_YELLOW);
                    case DARK_SELECTED -> result.append(SET_BG_COLOR_ORANGE);
                    case LIGHT_HIGHLIGHTED -> result.append(SET_BG_COLOR_GREEN);
                    case DARK_HIGHLIGHTED -> result.append(SET_BG_COLOR_DARK_GREEN);
                }

                if (piece == null) {
                    result.append(EMPTY);
                    continue;
                }

                pieceHandler(result, piece);
            }

            result.append(RESET_BG_COLOR).append('\n');
        }


        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    @SuppressWarnings("DuplicatedCode")
    private void pieceHandler(StringBuilder result, ChessPiece piece) {
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

    private ChessPosition positionHandler(String position) {
        int col = position.charAt(0) - 'a' + 1;
        int row = Character.getNumericValue(position.charAt(1));
        return new ChessPosition(row, col);
    }
}
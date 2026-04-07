package server;

import chess.ChessGame;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import model.JoinGameRequest;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserResignCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameService gameService;
    private final UserService userService;

    public WebSocketHandler(String databaseType) throws DataAccessException {
        gameService = new GameService(databaseType);
        userService = new UserService(databaseType);
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            if (!userService.isValidAuth(userGameCommand.getAuthToken())) {
                invalidAuth(ctx.session);
            }
            String username = userService.getUsername(userGameCommand.getAuthToken());
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> connect(userGameCommand.getGameID(), username, ctx.session);
                case LEAVE -> leave(userGameCommand.getGameID(), username, userGameCommand.getAuthToken(), ctx.session);
                case RESIGN -> resign(new Gson().fromJson(ctx.message(), UserResignCommand.class), username, ctx.session);
                case MAKE_MOVE -> makeMove(new Gson().fromJson(ctx.message(), MakeMoveCommand.class), username, ctx.session);
            }
        } catch (IOException | DataAccessException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Integer gameID, String username, Session session) throws IOException, DataAccessException {
        if (gameService.getGame(gameID) == null) {
            ErrorMessage errorMessage = new ErrorMessage("Error: invalid game ID");
            connections.sendMsg(session, errorMessage);
            return;
        }
        connections.add(gameID, session);
        ChessGame game = gameService.getGame(gameID).game();
        connections.sendMsg(session, new LoadGameMessage(game));
        var notification = new Notification(username + " has joined the game!");
        connections.broadcast(gameID, session, notification);
    }

    private void leave(Integer gameID, String username, String authToken, Session session) throws IOException {
        try {
            GameData game = gameService.getGame(gameID);
            String userColor = null;

            if (username.equals(game.whiteUsername())) {
                userColor = "WHITE";
            } else if (username.equals(game.blackUsername())) {
                userColor = "BLACK";
            }

            if (userColor != null) {
                gameService.leaveGame(new JoinGameRequest(userColor, gameID, authToken));
            }

            var notification = new Notification(username + " has left the game!");
            connections.broadcast(gameID, session, notification);
            connections.remove(gameID, session);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    private void resign(UserResignCommand command, String username, Session session) throws IOException {
        try {
            if (gameService.gameIsOver(command.getGameID())) {
                connections.sendMsg(session, new ErrorMessage("Error: game already over"));
                return;
            }
            gameService.resignGame(command.getAuthToken(), command.getGameID(), command.teamColor);
        } catch (DataAccessException e) {
            connections.sendMsg(session, new ErrorMessage(e.getMessage()));
            return;
        }
        var notification = new Notification(username + ": " + command.teamColor + " has resigned!");
        connections.broadcast(command.getGameID(), null, notification);
    }

    private void makeMove(MakeMoveCommand command, String username, Session session) throws IOException {
        try {
            if (gameService.gameIsOver(command.getGameID())) {
                var error = new ErrorMessage("Error: game already over");
                connections.sendMsg(session, error);
                return;
            }

            GameData oldGame = gameService.getGame(command.getGameID());
            boolean userIsNotWhite = !username.equals(oldGame.whiteUsername());
            boolean userIsNotBlack = !username.equals(oldGame.blackUsername());

            if (userIsNotWhite && userIsNotBlack) {
                var error = new ErrorMessage("Error: observers cannot make moves");
                connections.sendMsg(session, error);
                return;
            }

            ChessPiece piece = oldGame.game().getBoard().getPiece(command.getMove().getStartPosition());
            boolean userCannotMovePiece = userIsNotWhite && piece.getTeamColor() == ChessGame.TeamColor.WHITE ||
                    userIsNotBlack && piece.getTeamColor() == ChessGame.TeamColor.BLACK;

            if (userCannotMovePiece) {
                var error = new ErrorMessage("Error: cannot move opponent's pieces");
                connections.sendMsg(session, error);
                return;
            }

            var movedMessage = "Opponent moved";
            connections.broadcast(command.getGameID(), session, new Notification(movedMessage));

            ChessGame game = gameService.makeMove(command.getAuthToken(), command.getGameID(), command.getMove());
            connections.broadcast(command.getGameID(), null, new LoadGameMessage(game));
            ChessGame.TeamColor nextTeam;

            if (oldGame.game().getTeamTurn() == ChessGame.TeamColor.WHITE) {
                nextTeam = ChessGame.TeamColor.BLACK;
            } else {
                nextTeam = ChessGame.TeamColor.WHITE;
            }

            if (game.isInCheckmate(nextTeam)) {
                var message = nextTeam.name() + " is in checkmate!";
                connections.broadcast(command.getGameID(), null, new Notification(message));
            } else if (game.isInStalemate(nextTeam)) {
                var message = "Stalemate!";
                connections.broadcast(command.getGameID(), null, new Notification(message));
            } else if (game.isInCheck(nextTeam)) {
                var message = nextTeam.name() + " is in check!";
                connections.broadcast(command.getGameID(), null, new Notification(message));
            }
        } catch (DataAccessException e) {
            connections.sendMsg(session, new ErrorMessage("Internal server error"));
        } catch (InvalidMoveException e) {
            connections.sendMsg(session, new ErrorMessage("Error: invalid move"));
        }
    }

    private void invalidAuth(Session session) throws IOException {
        ErrorMessage errorMessage = new ErrorMessage("Invalid Authtoken");
        connections.sendMsg(session, errorMessage);
    }
}
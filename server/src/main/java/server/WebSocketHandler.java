package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.commands.UserResignCommand;
import websocket.messages.ErrorMessage;
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
            String username = userService.getUsername(userGameCommand.getAuthToken());
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> connect(userGameCommand.getGameID(), username, ctx.session);
                case LEAVE -> leave(userGameCommand.getGameID(), username, ctx.session);
                case RESIGN -> resign(new Gson().fromJson(ctx.message(), UserResignCommand.class), username, ctx.session);
            }
        } catch (IOException | DataAccessException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Integer gameID, String username, Session session) throws IOException {
        connections.add(gameID, session);
        var notification = new Notification(username + " has joined the game!");
        connections.broadcast(gameID, session, notification);
    }

    private void leave(Integer gameID, String username, Session session) throws IOException {
        var notification = new Notification(username + " has left the game!");
        connections.broadcast(gameID, session, notification);
        connections.remove(gameID, session);
    }

    private void resign(UserResignCommand command, String username, Session session) throws IOException {
        try {
            gameService.resignGame(command.getAuthToken(), command.getGameID(), command.teamColor);
        } catch (DataAccessException e) {
            connections.sendMsg(session, new ErrorMessage(e.getMessage()));
            return;
        }
        var notification = new Notification(username + ": " + command.teamColor + " has resigned!");
        connections.broadcast(command.getGameID(), null, notification);
    }
}
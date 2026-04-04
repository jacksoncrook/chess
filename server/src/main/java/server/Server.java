package server;

import dataaccess.DataAccessException;
import handler.*;
import io.javalin.*;

public class Server {

    private final Javalin javalin;
    private WebSocketHandler webSocketHandler;

    public Server() {

        String databaseType = "MySQL";
        try {
            webSocketHandler = new WebSocketHandler(databaseType);
        } catch (DataAccessException e) {
            System.out.println("Error in webSocketHandler creation: " + e.getMessage());
        }

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", new RegisterHandler(databaseType))
                .post("/session", new LoginHandler(databaseType))
                .delete("/session", new LogoutHandler(databaseType))
                .post("/game", new CreateGameHandler(databaseType))
                .get("/game", new ListGamesHandler(databaseType))
                .put("/game", new JoinGameHandler(databaseType))
                .delete("/db", new ClearHandler(databaseType))
                .ws("/ws", ws -> {
                    ws.onConnect(webSocketHandler);
                    ws.onMessage(webSocketHandler);
                    ws.onClose(webSocketHandler);
                });


    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

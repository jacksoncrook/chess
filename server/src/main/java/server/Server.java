package server;

import handler.*;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        String databaseType = "MySQL";
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", new RegisterHandler(databaseType))
                .post("/session", new LoginHandler(databaseType))
                .delete("/session", new LogoutHandler(databaseType))
                .post("/game", new CreateGameHandler(databaseType))
                .get("/game", new ListGamesHandler(databaseType))
                .put("/game", new JoinGameHandler(databaseType))
                .delete("/db", new ClearHandler(databaseType));

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

package server;

import handler.*;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", new RegisterHandler())
                .post("/session", new LoginHandler())
                .delete("/session", new LogoutHandler())
                .post("/game", new CreateGameHandler())
                .get("/game", new ListGamesHandler())
                .delete("/db", new ClearHandler());

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

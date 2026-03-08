package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.JoinGameRequest;
import org.jetbrains.annotations.NotNull;
import service.GameService;

public class JoinGameHandler extends HttpHandler {
    public JoinGameHandler(String databaseType) {
        super(databaseType);
    }

    public JoinGameRequest fromJson(Context context) {
        return new Gson().fromJson(context.body(), JoinGameRequest.class);
    }

    @Override
    public void handle(@NotNull Context context) {
        JoinGameRequest joinGameRequest = fromJson(context);
        joinGameRequest = joinGameRequest.addAuth(context.header("Authorization"));

        try {
            new GameService(databaseType).joinGame(joinGameRequest);
            context.status(200);

        } catch (DataAccessException e) {
            interpretException(e, context);
        }
    }
}

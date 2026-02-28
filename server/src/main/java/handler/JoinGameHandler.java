package handler;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.ErrorMessage;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.JoinGameRequest;
import org.jetbrains.annotations.NotNull;
import service.GameService;

public class JoinGameHandler implements Handler {
    public JoinGameRequest fromJson(Context context) {
        return new Gson().fromJson(context.body(), JoinGameRequest.class);
    }

    @Override
    public void handle(@NotNull Context context) {
        JoinGameRequest joinGameRequest = fromJson(context);
        joinGameRequest = joinGameRequest.addAuth(context.header("Authorization"));

        try {
            new GameService().joinGame(joinGameRequest);
            context.status(200);

        } catch (DataAccessException e) {
            ErrorMessage message = new ErrorMessage(e.getMessage());
            String errorMessage = new Gson().toJson(message);

            if (e.getClass() == UnauthorizedException.class) {
                context.status(401);
                context.json(errorMessage);

            } else if (e.getClass() == AlreadyTakenException.class) {
                context.status(403);
                context.json(errorMessage);

            } else {
                context.status(400);
                context.result(errorMessage);
            }
        }
    }
}

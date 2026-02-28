package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ErrorMessage;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.CreateGameRequest;
import model.CreateGameResult;
import org.jetbrains.annotations.NotNull;
import service.GameService;

public class CreateGameHandler implements Handler {
    public CreateGameRequest fromJson(Context context) {
        return new Gson().fromJson(context.body(), CreateGameRequest.class);
    }

    public String toJson(CreateGameResult createGameResult) {
        return new Gson().toJson(createGameResult);
    }

    @Override
    public void handle(@NotNull Context context) {
        CreateGameRequest createGameRequest = fromJson(context);
        createGameRequest = createGameRequest.addAuth(context.header("Authorization"));
        try {
            CreateGameResult createGameResult = new GameService().createGame(createGameRequest);
            context.status(200);
            context.json(toJson(createGameResult));
        } catch (DataAccessException e) {
            ErrorMessage message = new ErrorMessage(e.getMessage());
            String errorMessage = new Gson().toJson(message);
            if (e.getClass() == UnauthorizedException.class) {
                context.status(401);
                context.json(errorMessage);
            } else {
                context.status(400);
                context.result(errorMessage);
            }
        }
    }
}

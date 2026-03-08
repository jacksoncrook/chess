package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.CreateGameRequest;
import model.CreateGameResult;
import org.jetbrains.annotations.NotNull;
import service.GameService;

public class CreateGameHandler extends HttpHandler {
    public CreateGameHandler(String databaseType) {
        super(databaseType);
    }

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
            CreateGameResult createGameResult = new GameService(databaseType).createGame(createGameRequest);
            context.status(200);
            context.json(toJson(createGameResult));

        } catch (DataAccessException e) {
            interpretException(e, context);
        }
    }
}

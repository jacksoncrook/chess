package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.GetGamesRequest;
import model.GetGamesResult;
import org.jetbrains.annotations.NotNull;
import service.GameService;

public class ListGamesHandler extends HttpHandler {
    public GetGamesRequest fromJson(Context context) {
        return new GetGamesRequest(context.header("Authorization"));
    }

    public String toJson(GetGamesResult gameData) {
        return new Gson().toJson(gameData);
    }

    @Override
    public void handle(@NotNull Context context) {
        GetGamesRequest getGamesRequest = fromJson(context);

        try {
            GetGamesResult gameList = new GameService().listGames(getGamesRequest);
            context.status(200);
            context.json(toJson(gameList));

        } catch (DataAccessException e) {
            interpretException(e, context);
        }
    }
}

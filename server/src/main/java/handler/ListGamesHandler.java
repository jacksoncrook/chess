package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ErrorMessage;
import dataaccess.GameDAO;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import model.GameData;
import model.GetGamesRequest;
import org.jetbrains.annotations.NotNull;
import service.GameService;

import java.util.Collection;

public class ListGamesHandler implements Handler {
    public GetGamesRequest fromJson(Context context) {
        return new GetGamesRequest(context.header("Authorization"));
    }

    public String toJson(Collection<GameData> gameData) {
        return new Gson().toJson(gameData);
    }

    @Override
    public void handle(@NotNull Context context) {
        GetGamesRequest getGamesRequest = fromJson(context);
        try {
            Collection<GameData> gameList = new GameService().listGames(getGamesRequest);
            context.status(200);
            context.json(toJson(gameList));

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

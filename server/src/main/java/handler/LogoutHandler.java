package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ErrorMessage;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.LogoutRequest;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class LogoutHandler implements Handler {
    public LogoutRequest fromJson(Context context) {
        return new LogoutRequest(context.header("Authorization"));
    }

    @Override
    public void handle(@NotNull Context context) {
        LogoutRequest logoutRequest = fromJson(context);
        try {
            new UserService().logout(logoutRequest);
            context.status(200);
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

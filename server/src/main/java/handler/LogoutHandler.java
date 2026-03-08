package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.LogoutRequest;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class LogoutHandler extends HttpHandler {
    public LogoutHandler(String databaseType) {
        super(databaseType);
    }

    public LogoutRequest fromJson(Context context) {
        return new LogoutRequest(context.header("Authorization"));
    }

    @Override
    public void handle(@NotNull Context context) {
        LogoutRequest logoutRequest = fromJson(context);

        try {
            new UserService(databaseType).logout(logoutRequest);
            context.status(200);

        } catch (DataAccessException e) {
            interpretException(e, context);
        }
    }
}

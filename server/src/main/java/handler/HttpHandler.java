package handler;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public abstract class HttpHandler implements Handler {

    record ErrorMessage(String message) {}
    public static String databaseType;

    public HttpHandler(String databaseType) {
        HttpHandler.databaseType = databaseType;
    }

    public void interpretException(DataAccessException e, Context context) {
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

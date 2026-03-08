package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.GameService;

public class ClearHandler extends HttpHandler {

    public ClearHandler(String databaseType) {
        super(databaseType);
    }

    @Override
    public void handle(@NotNull Context context) {
        try {
            new GameService(databaseType).clear();
            context.status(200);
        } catch (DataAccessException e) {
            interpretException(e, context);
            context.status(500);
        }
    }
}

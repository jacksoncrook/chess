package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.GameService;

public class ClearHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) {
        new GameService().clear();
        context.status(200);
    }
}

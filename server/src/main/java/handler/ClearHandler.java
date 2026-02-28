package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class ClearHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        context.status(200);
        context.result("OK");
    }
}

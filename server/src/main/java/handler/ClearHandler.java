package handler;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class ClearHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) {
        new MemoryUserDAO().clear();
        new MemoryAuthDAO().clear();
        new MemoryGameDAO().clear();
        context.status(200);
    }
}

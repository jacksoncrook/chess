package handler;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class RegisterHandler extends HTTPHandler {

    public Context ctx;

    public RegisterHandler() {
        //ctx = context;
    }

    @Override
    public String toJson() {
        return "";
    }

    @Override
    public void handle(@NotNull Context context) throws DataAccessException {
        HTTPRequest httpRequest = fromJson(context);
        if (true) {
            context.status(200);
            context.result("OK");
        } else {
            context.status(400);
            context.result("Error: not authorized");
        }
    }
}

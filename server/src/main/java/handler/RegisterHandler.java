package handler;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.RegisterRequest;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class RegisterHandler implements Handler {

    public RegisterHandler() {
    }

    public model.UserData fromJson(Context context) {
        return new Gson().fromJson(context.body(), model.UserData.class);
    }

    public String toJson() {
        return "";
    }

    @Override
    public void handle(@NotNull Context context) throws DataAccessException {
        model.UserData registerRequest = fromJson(context);
        try {
            model.AuthData registerResult = new UserService().register(registerRequest);
        } catch (DataAccessException e) {
            context.status(400);
            context.result("Error: not authorized");
        }
        if (true) {
            context.status(200);
            context.result("OK");
        } else {

        }
    }
}

package handler;
import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.UserData;
import model.AuthData;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class RegisterHandler implements Handler {

    public model.UserData fromJson(Context context) {
        return new Gson().fromJson(context.body(), UserData.class);
    }

    public String toJson(AuthData authData) {
        return new Gson().toJson(authData);
    }

    @Override
    public void handle(@NotNull Context context) {
        UserData registerRequest = fromJson(context);
        try {
            AuthData registerResult = new UserService().register(registerRequest);
            context.status(200);
            context.result("OK");
            context.json(toJson(registerResult));
        } catch (DataAccessException e) {

            if (e.getClass() == AlreadyTakenException.class) {
                context.status(403);
                context.result("Error: already taken");
            } else {
                context.status(400);
                context.result("Error: bad request");
            }
        }
    }
}

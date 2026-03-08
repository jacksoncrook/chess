package handler;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.UserData;
import model.AuthData;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class RegisterHandler extends HttpHandler {

    public RegisterHandler(String databaseType) {
        super(databaseType);
    }

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
            AuthData registerResult = new UserService(databaseType).register(registerRequest);
            context.status(200);
            context.json(toJson(registerResult));

        } catch (DataAccessException e) {
            interpretException(e, context);
        }
    }
}

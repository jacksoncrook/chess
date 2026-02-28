package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.AuthData;
import model.LoginRequest;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class LoginHandler extends HttpHandler {
    public LoginRequest fromJson(Context context) {
        return new Gson().fromJson(context.body(), LoginRequest.class);
    }

    public String toJson(AuthData authData) {
        return new Gson().toJson(authData);
    }

    @Override
    public void handle(@NotNull Context context) {
        LoginRequest loginRequest = fromJson(context);

        try {
            AuthData loginResult = new UserService().login(loginRequest);
            context.status(200);
            context.json(toJson(loginResult));

        } catch (DataAccessException e) {
            interpretException(e, context);
        }
    }
}

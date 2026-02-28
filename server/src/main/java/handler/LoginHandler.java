package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ErrorMessage;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import model.LoginRequest;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class LoginHandler implements Handler {
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
            ErrorMessage message = new ErrorMessage(e.getMessage());
            String errorMessage = new Gson().toJson(message);
            if (e.getClass() == UnauthorizedException.class) {
                context.status(401);
                context.json(errorMessage);
            } else {
                context.status(400);
                context.result(errorMessage);
            }
        }
    }
}

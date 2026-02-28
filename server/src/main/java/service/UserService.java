package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public model.AuthData register(UserData registerRequest) throws DataAccessException {
        UserData userData = new MemoryUserDAO().getUser(registerRequest.username());
        if (userData != null) {
            throw new AlreadyTakenException("Username already taken");
        } else {
            String authToken = generateToken();
            AuthData authData = new AuthData(authToken, registerRequest.username());
            return authData;
        }
    }

    public model.AuthData login(model.LoginRequest loginRequest) {
        return new AuthData("", "");
    }
    public void logout(model.LogoutRequest logoutRequest) {}
}
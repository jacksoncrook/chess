package service;

import dataaccess.*;
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
            new MemoryAuthDAO().createAuth(authData);
            return authData;
        }
    }

    public model.AuthData login(model.LoginRequest loginRequest) throws DataAccessException {
        UserData userData = new MemoryUserDAO().getUser(loginRequest.username());
        if (userData == null) {
            throw new BadRequestException("Invalid username");
        } else if (!userData.password().equals(loginRequest.password())) {
            throw new UnauthorizedException("Username and password don't match");
        } else {
            String authToken = generateToken();
            AuthData authData = new AuthData(authToken, loginRequest.username());
            new MemoryAuthDAO().createAuth(authData);
            return authData;
        }
    }

    public void logout(model.LogoutRequest logoutRequest) {}
}
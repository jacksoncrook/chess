package service;

import dataaccess.*;
import model.AuthData;
import model.LoginRequest;
import model.LogoutRequest;
import model.UserData;

import java.util.UUID;

public class UserService {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }


    public AuthData register(UserData registerRequest) throws DataAccessException {
        if (registerRequest.password() == null || registerRequest.username() == null || registerRequest.email() == null) {
            throw new BadRequestException("Error: bad request");
        }

        UserData userData = new MemoryUserDAO().getUser(registerRequest.username());

        if (userData != null) {
            throw new AlreadyTakenException("Error: already taken");

        } else {
            new MemoryUserDAO().createUser(registerRequest);

            String authToken = generateToken();
            AuthData authData = new AuthData(authToken, registerRequest.username());

            new MemoryAuthDAO().createAuth(authData);

            return authData;
        }
    }


    public AuthData login(LoginRequest loginRequest) throws DataAccessException {
        if (loginRequest.password() == null || loginRequest.username() == null) {
            throw new BadRequestException("Error: bad request");
        }

        UserData userData = new MemoryUserDAO().getUser(loginRequest.username());

        if (userData == null) {
            throw new UnauthorizedException("Error: unauthorized");

        } else if (!userData.password().equals(loginRequest.password())) {
            throw new UnauthorizedException("Error: unauthorized");

        } else {

            String authToken = generateToken();
            AuthData authData = new AuthData(authToken, loginRequest.username());

            new MemoryAuthDAO().createAuth(authData);

            return authData;
        }
    }


    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        if (logoutRequest == null) {
            throw new UnauthorizedException("Error: unauthorized");

        } else if (logoutRequest.authToken() == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        AuthData authData = new MemoryAuthDAO().getAuth(logoutRequest.authToken());

        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");

        } else {
            new MemoryAuthDAO().deleteAuth(authData);
        }
    }
}
package service;

import dataaccess.*;
import model.AuthData;
import model.LoginRequest;
import model.LogoutRequest;
import model.UserData;

import java.util.UUID;

public class UserService {
    private static AuthDAO authDAO;
    private static UserDAO userDAO;

    public UserService(String databaseType) throws DataAccessException {
        if (databaseType != null && databaseType.equals("Memory")) {
            authDAO = new MemoryAuthDAO();
            userDAO = new MemoryUserDAO();
        } else {
            authDAO = new MySqlAuthDAO();
            userDAO = new MySqlUserDAO();
        }
    }
    
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData register(UserData registerRequest) throws DataAccessException {
        if (registerRequest.password() == null || registerRequest.username() == null || registerRequest.email() == null) {
            throw new BadRequestException("Error: bad request");
        }

        boolean userExists = userDAO.doesUserExist(registerRequest.username());

        if (userExists) {
            throw new AlreadyTakenException("Error: already taken");

        } else {
            userDAO.createUser(registerRequest);

            String authToken = generateToken();
            AuthData authData = new AuthData(authToken, registerRequest.username());

            authDAO.createAuth(authData);

            return authData;
        }
    }


    public AuthData login(LoginRequest loginRequest) throws DataAccessException {
        if (loginRequest.password() == null || loginRequest.username() == null) {
            throw new BadRequestException("Error: bad request");
        }

        boolean userExists = userDAO.doesUserExist(loginRequest.username());

        if (!userExists) {
            throw new UnauthorizedException("Error: unauthorized");

        } else if (!userDAO.verifyUser(loginRequest)) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, loginRequest.username());

        authDAO.createAuth(authData);

        return authData;

    }


    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        if (logoutRequest == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        AuthData authData = authDAO.getAuth(logoutRequest.authToken());

        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        authDAO.deleteAuth(authData);
    }
}
package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTests {

    private static GameService gameService;
    private static UserService userService;
    private static UserData existingUser;
    private static LoginRequest existingUserLogin;
    private static UserData newUser;
    private String existingAuth;


    // ### TESTING SETUP/CLEANUP ###

    @BeforeAll
    public static void init() {
        DataAccessException exception = null;

        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        existingUserLogin = new LoginRequest(existingUser.username(), existingUser.password());
        newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");

        try {
            gameService = new GameService("Memory");
            userService = new UserService("Memory");
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception);
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        gameService.clear();

        //one userService already logged in
        AuthData regResult = userService.register(existingUser);
        existingAuth = regResult.authToken();
    }

    // ### UNIT TESTS ###

    @Test
    @Order(1)
    @DisplayName("Normal User Registration")
    public void registerSuccess() throws DataAccessException {
        AuthData registerResult = userService.register(newUser);

        Assertions.assertEquals(newUser.username(), registerResult.username(),
                "Response did not give the same username as userService");
        Assertions.assertNotNull(registerResult.authToken(), "Response did not return authentication String");
    }

    @Test
    @Order(2)
    @DisplayName("Duplicate User Registration")
    public void registerTwice() {
        try {
            AuthData registerResult = userService.register(existingUser);
            Assertions.assertNull(registerResult, "Response was not null");

        } catch (DataAccessException e) {
            Assertions.assertEquals(AlreadyTakenException.class, e.getClass(), "Incorrect exception thrown");
        }
    }

    @Test
    @Order(3)
    @DisplayName("Missing Registration Information")
    public void registerBadRequest() {
        UserData[] incompleteRegisterRequests = {
                new UserData(null, newUser.password(), newUser.email()),
                new UserData(newUser.username(), null, newUser.email()),
                new UserData(newUser.username(), newUser.password(), null)
        };

        for (UserData incompleteRegisterRequest : incompleteRegisterRequests) {

            try {
                AuthData registerResult = userService.register(incompleteRegisterRequest);
                Assertions.assertNull(registerResult, "Response was not null");

            } catch (DataAccessException e) {
                Assertions.assertEquals(BadRequestException.class, e.getClass(), "Incorrect exception thrown");
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("Normal User Login")
    public void loginSuccess() throws DataAccessException {
        AuthData loginResult = userService.login(existingUserLogin);

        Assertions.assertEquals(existingUserLogin.username(), loginResult.username(),
                "Response did not give the same username as userService");
        Assertions.assertNotNull(loginResult.authToken(), "Response did not return authentication String");
    }

    @Test
    @Order(5)
    @DisplayName("Missing User Information")
    public void loginBadRequest() {
        LoginRequest[] incompleteLoginRequests = {
                new LoginRequest(null, existingUser.password()),
                new LoginRequest(existingUser.username(), null),
        };

        for (LoginRequest incompleteLoginRequest : incompleteLoginRequests) {

            try {
                AuthData loginResult = userService.login(incompleteLoginRequest);
                Assertions.assertNull(loginResult, "Response was not null");

            } catch (DataAccessException e) {
                Assertions.assertEquals(BadRequestException.class, e.getClass(), "Incorrect exception thrown");
            }
        }
    }

    @Test
    @Order(6)
    @DisplayName("Incorrect User Password")
    public void loginWrongPassword() {
        LoginRequest loginRequest = new LoginRequest(existingUserLogin.username(), "WRONG PASSWORD");

        try {
            AuthData loginResult = userService.login(loginRequest);
            Assertions.assertNull(loginResult, "Response was not null");

        } catch (DataAccessException e) {
            Assertions.assertEquals(UnauthorizedException.class, e.getClass(), "Incorrect exception thrown");
        }
    }

    @Test
    @Order(7)
    @DisplayName("Invalid Username")
    public void loginBadUsername() {
        LoginRequest loginRequest = new LoginRequest("unique username", existingUserLogin.password());

        try {
            AuthData loginResult = userService.login(loginRequest);
            Assertions.assertNull(loginResult, "Response was not null");

        } catch (DataAccessException e) {
            Assertions.assertEquals(UnauthorizedException.class, e.getClass(), "Incorrect exception thrown");
        }
    }

    @Test
    @Order(8)
    @DisplayName("Normal User Logout")
    public void logoutSuccess() {
        LogoutRequest logoutRequest = new LogoutRequest(existingAuth);

        try {
            userService.logout(logoutRequest);
        } catch (DataAccessException e) {
            Assertions.assertNull(e, "Unexpected exception thrown");
        }
    }

    @Test
    @Order(9)
    @DisplayName("User Logout Twice")
    public void logoutTwice() {
        LogoutRequest logoutRequest = new LogoutRequest(existingAuth);
        DataAccessException exception = null;

        try {
            userService.logout(logoutRequest);
        } catch (DataAccessException e) {
            Assertions.assertNull(e, "Unexpected exception thrown");
        }

        try {
            userService.logout(logoutRequest);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(UnauthorizedException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(10)
    @DisplayName("Bad Auth User Logout")
    public void badAuthLogout() {
        LogoutRequest logoutRequest = new LogoutRequest("invalid auth");
        DataAccessException exception = null;

        try {
            userService.logout(logoutRequest);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(UnauthorizedException.class, exception.getClass(), "Incorrect exception thrown");
    }
}
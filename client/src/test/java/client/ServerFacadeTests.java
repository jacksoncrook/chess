package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static UserData existingUser;
    private static LoginRequest existingUserLogin;
    private static UserData newUser;
    private String existingAuth;
    private int existingGameID;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        
        serverFacade = new ServerFacade("http://localhost:" + port);
        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        existingUserLogin = new LoginRequest(existingUser.username(), existingUser.password());
        newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void setup() throws Exception {
        serverFacade.clear();

        //one user logged in with valid auth
        AuthData regResult = serverFacade.register(existingUser);
        existingAuth = regResult.authToken();

        CreateGameRequest existingGameRequest = new CreateGameRequest(existingAuth, "existingGameName");
        existingGameID = serverFacade.createGame(existingGameRequest).gameID();
    }

    // ### UNIT TESTS ###

    @Test
    @Order(1)
    @DisplayName("Normal Game Creation")
    public void createGameSuccess() {
        CreateGameRequest newGameRequest = new CreateGameRequest(existingAuth, "newGameName");
        Exception exception = null;
        CreateGameResult response = null;

        try {
            response = serverFacade.createGame(newGameRequest);
        } catch (Exception e) {
            exception = e;
        }

        Assertions.assertNotNull(response, "Response was unexpectedly null");
        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(2)
    @DisplayName("Invalid Auth Game Creation")
    public void createGameBadAuth() {
        CreateGameRequest newGameRequest = new CreateGameRequest(null, "newGameName");
        Exception exception = null;

        try {
            serverFacade.createGame(newGameRequest);
        } catch (Exception e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
    }

    @Test
    @Order(3)
    @DisplayName("Invalid Name Game Creation")
    public void createGameBadName() {
        CreateGameRequest newGameRequest = new CreateGameRequest(existingAuth, null);
        Exception exception = null;

        try {
            serverFacade.createGame(newGameRequest);
        } catch (Exception e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
    }

    @Test
    @Order(4)
    @DisplayName("Normal List Games")
    public void listGamesSuccess() {
        GetGamesRequest getGamesRequest = new GetGamesRequest(existingAuth);
        Exception exception = null;

        try {
            model.GetGamesResult getGamesResult = serverFacade.listGames(getGamesRequest);
            Assertions.assertNotNull(getGamesResult, "Game list wasn't properly returned");
        } catch (Exception e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(5)
    @DisplayName("Invalid Auth Game Creation")
    public void listGamesBadAuth() {
        GetGamesRequest getGamesRequest = new GetGamesRequest(null);
        Exception exception = null;

        try {
            model.GetGamesResult getGamesResult = serverFacade.listGames(getGamesRequest);
            Assertions.assertNull(getGamesResult, "Invalid auth didn't return null");
        } catch (Exception e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
    }

    @Test
    @Order(6)
    @DisplayName("Normal Join Game As White")
    public void joinGameSuccessWhite() {
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", existingGameID, existingAuth);
        Exception exception = null;

        try {
            serverFacade.joinGame(joinGameRequest);
        } catch (Exception e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(7)
    @DisplayName("Normal Join Game As Black")
    public void joinGameSuccessBlack() {
        JoinGameRequest joinGameRequest = new JoinGameRequest("BLACK", existingGameID, existingAuth);
        Exception exception = null;

        try {
            serverFacade.joinGame(joinGameRequest);
        } catch (Exception e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(8)
    @DisplayName("Invalid Auth Join Game")
    public void joinGameBadAuth() {
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", existingGameID, null);
        Exception exception = null;

        try {
            serverFacade.joinGame(joinGameRequest);
        } catch (Exception e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
    }

    @Test
    @Order(9)
    @DisplayName("Invalid Team Join Game")
    public void joinGameBadTeamColor() {
        JoinGameRequest joinGameRequest = new JoinGameRequest("RED", existingGameID, null);
        Exception exception = null;

        try {
            serverFacade.joinGame(joinGameRequest);
        } catch (Exception e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
    }

    @Test
    @Order(10)
    @DisplayName("Team Taken Join Game")
    public void joinGameTeamTaken() throws Exception {
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", existingGameID, existingAuth);
        Exception exception = null;

        UserData secondExistingUser = new UserData("ExistingUserTwo", "existingUserPassword", "eu2@mail.com");
        AuthData regResult = serverFacade.register(secondExistingUser);
        String secondExistingAuth = regResult.authToken();

        JoinGameRequest secondJoinGameRequest = new JoinGameRequest("WHITE", existingGameID, secondExistingAuth);

        try {
            serverFacade.joinGame(joinGameRequest);
        } catch (Exception e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");

        try {
            serverFacade.joinGame(secondJoinGameRequest);
        } catch (Exception e) {
            exception = e;
        }


        Assertions.assertNotNull(exception, "Expected exception not thrown");
    }

    @Test
    @Order(11)
    @DisplayName("Normal User Registration")
    public void registerSuccess() throws Exception {
        AuthData registerResult = serverFacade.register(newUser);

        Assertions.assertEquals(newUser.username(), registerResult.username(),
                "Response did not give the same username as serverFacade");
        Assertions.assertNotNull(registerResult.authToken(), "Response did not return authentication String");
    }

    @Test
    @Order(12)
    @DisplayName("Duplicate User Registration")
    public void registerTwice() {
        Exception exception = null;
        try {
            AuthData registerResult = serverFacade.register(existingUser);
            Assertions.assertNull(registerResult, "Response was not null");

        } catch (Exception e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
    }

    @Test
    @Order(13)
    @DisplayName("Missing Registration Information")
    public void registerBadRequest() {
        UserData[] incompleteRegisterRequests = {
                new UserData(null, newUser.password(), newUser.email()),
                new UserData(newUser.username(), null, newUser.email()),
                new UserData(newUser.username(), newUser.password(), null)
        };

        for (UserData incompleteRegisterRequest : incompleteRegisterRequests) {
            Exception exception = null;

            try {
                AuthData registerResult = serverFacade.register(incompleteRegisterRequest);
                Assertions.assertNull(registerResult, "Response was not null");

            } catch (Exception e) {
                exception = e;
            }

            Assertions.assertNotNull(exception, "Expected exception not thrown");
        }
    }

    @Test
    @Order(14)
    @DisplayName("Normal User Login")
    public void loginSuccess() throws Exception {
        AuthData loginResult = serverFacade.login(existingUserLogin);

        Assertions.assertEquals(existingUserLogin.username(), loginResult.username(),
                "Response did not give the same username as serverFacade");
        Assertions.assertNotNull(loginResult.authToken(), "Response did not return authentication String");
    }

    @Test
    @Order(15)
    @DisplayName("Missing User Information")
    public void loginBadRequest() {
        LoginRequest[] incompleteLoginRequests = {
                new LoginRequest(null, existingUser.password()),
                new LoginRequest(existingUser.username(), null),
        };

        for (LoginRequest incompleteLoginRequest : incompleteLoginRequests) {
            Exception exception = null;

            try {
                AuthData loginResult = serverFacade.login(incompleteLoginRequest);
                Assertions.assertNull(loginResult, "Response was not null");

            } catch (Exception e) {
                exception = e;
            }

            Assertions.assertNotNull(exception, "Expected exception not thrown");
        }
    }

    @Test
    @Order(16)
    @DisplayName("Incorrect User Password")
    public void loginWrongPassword() {
        LoginRequest loginRequest = new LoginRequest(existingUserLogin.username(), "WRONG PASSWORD");
        Exception exception = null;

        try {
            AuthData loginResult = serverFacade.login(loginRequest);
            Assertions.assertNull(loginResult, "Response was not null");

        } catch (Exception e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
    }

    @Test
    @Order(17)
    @DisplayName("Invalid Username")
    public void loginBadUsername() {
        LoginRequest loginRequest = new LoginRequest("unique username", existingUserLogin.password());
        Exception exception = null;

        try {
            AuthData loginResult = serverFacade.login(loginRequest);
            Assertions.assertNull(loginResult, "Response was not null");

        } catch (Exception e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
    }

    @Test
    @Order(18)
    @DisplayName("Normal User Logout")
    public void logoutSuccess() {
        LogoutRequest logoutRequest = new LogoutRequest(existingAuth);

        try {
            serverFacade.logout(logoutRequest);
        } catch (Exception e) {
            Assertions.assertNull(e, "Unexpected exception thrown");
        }
    }

    @Test
    @Order(19)
    @DisplayName("User Logout Twice")
    public void logoutTwice() {
        LogoutRequest logoutRequest = new LogoutRequest(existingAuth);
        Exception exception = null;

        try {
            serverFacade.logout(logoutRequest);
        } catch (Exception e) {
            Assertions.assertNull(e, "Unexpected exception thrown");
        }

        try {
            serverFacade.logout(logoutRequest);
        } catch (Exception e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
    }

    @Test
    @Order(20)
    @DisplayName("Bad Auth User Logout")
    public void badAuthLogout() {
        LogoutRequest logoutRequest = new LogoutRequest("invalid auth");
        Exception exception = null;

        try {
            serverFacade.logout(logoutRequest);
        } catch (Exception e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
    }

    @Test
    @Order(21)
    @DisplayName("Clear")
    public void clearSuccess() {
        Exception exception = null;

        try {
            serverFacade.clear();
        } catch (Exception e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }
}

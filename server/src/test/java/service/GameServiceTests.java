package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameServiceTests {

    private static final GameService gameService = new GameService();
    private static final UserService userService = new UserService();
    private static UserData existingUser;
    private String existingAuth;
    private int existingGameID;


    // ### TESTING SETUP/CLEANUP ###

    @BeforeAll
    public static void init() {
        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        gameService.clear();

        //one user logged in with valid auth
        AuthData regResult = userService.register(existingUser);
        existingAuth = regResult.authToken();

        CreateGameRequest existingGameRequest = new CreateGameRequest(existingAuth, "existingGameName");
        existingGameID = gameService.createGame(existingGameRequest).gameID();
    }

    // ### UNIT TESTS ###

    @Test
    @Order(1)
    @DisplayName("Normal Game Creation")
    public void createGameSuccess() {
        CreateGameRequest newGameRequest = new CreateGameRequest(existingAuth, "newGameName");
        DataAccessException exception = null;

        try {
            gameService.createGame(newGameRequest);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(2)
    @DisplayName("Invalid Auth Game Creation")
    public void createGameBadAuth() {
        CreateGameRequest newGameRequest = new CreateGameRequest(null, "newGameName");
        DataAccessException exception = null;

        try {
            gameService.createGame(newGameRequest);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(UnauthorizedException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(3)
    @DisplayName("Invalid Name Game Creation")
    public void createGameBadName() {
        CreateGameRequest newGameRequest = new CreateGameRequest(existingAuth, null);
        DataAccessException exception = null;

        try {
            gameService.createGame(newGameRequest);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(BadRequestException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(4)
    @DisplayName("Normal List Games")
    public void listGamesSuccess() {
        GetGamesRequest getGamesRequest = new GetGamesRequest(existingAuth);
        DataAccessException exception = null;

        try {
            GetGamesResult getGamesResult = gameService.listGames(getGamesRequest);
            Assertions.assertNotNull(getGamesResult, "Game list wasn't properly returned");
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(5)
    @DisplayName("Invalid Auth Game Creation")
    public void listGamesBadAuth() {
        GetGamesRequest getGamesRequest = new GetGamesRequest(null);
        DataAccessException exception = null;

        try {
            GetGamesResult getGamesResult = gameService.listGames(getGamesRequest);
            Assertions.assertNull(getGamesResult, "Invalid auth didn't return null");
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(UnauthorizedException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(6)
    @DisplayName("Normal Join Game As White")
    public void joinGameSuccessWhite() {
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", existingGameID, existingAuth);
        DataAccessException exception = null;

        try {
            gameService.joinGame(joinGameRequest);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(7)
    @DisplayName("Normal Join Game As Black")
    public void joinGameSuccessBlack() {
        JoinGameRequest joinGameRequest = new JoinGameRequest("BLACK", existingGameID, existingAuth);
        DataAccessException exception = null;

        try {
            gameService.joinGame(joinGameRequest);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(8)
    @DisplayName("Invalid Auth Join Game")
    public void joinGameBadAuth() {
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", existingGameID, null);
        DataAccessException exception = null;

        try {
            gameService.joinGame(joinGameRequest);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(UnauthorizedException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(9)
    @DisplayName("Invalid Team Join Game")
    public void joinGameBadTeamColor() {
        JoinGameRequest joinGameRequest = new JoinGameRequest("RED", existingGameID, null);
        DataAccessException exception = null;

        try {
            gameService.joinGame(joinGameRequest);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(BadRequestException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(10)
    @DisplayName("Team Taken Join Game")
    public void joinGameTeamTaken() throws DataAccessException {
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", existingGameID, existingAuth);
        DataAccessException exception = null;

        UserData secondExistingUser = new UserData("ExistingUserTwo", "existingUserPassword", "eu2@mail.com");
        AuthData regResult = userService.register(secondExistingUser);
        String secondExistingAuth = regResult.authToken();

        JoinGameRequest secondJoinGameRequest = new JoinGameRequest("WHITE", existingGameID, secondExistingAuth);

        try {
            gameService.joinGame(joinGameRequest);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");

        try {
            gameService.joinGame(secondJoinGameRequest);
        } catch (DataAccessException e) {
            exception = e;
        }


        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(AlreadyTakenException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(11)
    @DisplayName("Clear")
    public void clearSuccess() {
        gameService.clear();

        Assertions.assertTrue(MemoryGameDAO.gameDataTable.isEmpty(), "Game data table not empty");
        Assertions.assertTrue(MemoryUserDAO.userDataTable.isEmpty(), "User data table not empty");
        Assertions.assertTrue(MemoryAuthDAO.authDataTable.isEmpty(), "Auth data table not empty");
    }
}
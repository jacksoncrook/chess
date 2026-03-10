package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collection;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameDAOTests {
    private static GameDAO sqlGameDAO;
    private static GameData existingGameData;

    // ### TESTING SETUP/CLEANUP ###

    @BeforeAll
    public static void init() {
        DataAccessException exception = null;
        existingGameData = new GameData(10, "whiteUser", "blackUser", "gameName", new ChessGame());

        try {
            sqlGameDAO = new MySqlGameDAO();
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception);
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        sqlGameDAO.clear();
        sqlGameDAO.createGame(existingGameData);
    }

    // ### UNIT TESTS ###

    @Test
    @Order(1)
    @DisplayName("Clear Success")
    public void clearSuccess() {
        DataAccessException exception = null;

        try {
            sqlGameDAO.clear();
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(2)
    @DisplayName("Create Success")
    public void createSuccess() {
        DataAccessException exception = null;

        GameData newGame = new GameData(100, null, null, "name", new ChessGame());

        try {
            sqlGameDAO.createGame(newGame);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(3)
    @DisplayName("Creation Failure Bad White Username")
    public void createBadWhiteUsername() {
        DataAccessException exception = null;

        GameData newGame = new GameData(100, "bad Username!;", null, "name", new ChessGame());

        try {
            sqlGameDAO.createGame(newGame);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(BadRequestException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(4)
    @DisplayName("Creation Failure Bad Black Username")
    public void createBadBlackUsername() {
        DataAccessException exception = null;

        GameData newGame = new GameData(100,  null, "bad Username!;", "name", new ChessGame());

        try {
            sqlGameDAO.createGame(newGame);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(BadRequestException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(5)
    @DisplayName("Creation Failure Bad Game Name")
    public void createBadGameName() {
        DataAccessException exception = null;

        GameData newGame = new GameData(100,  null, null, "invalid game name'; ERROR()]", new ChessGame());

        try {
            sqlGameDAO.createGame(newGame);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(BadRequestException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(6)
    @DisplayName("Get Game Success")
    public void getGameSuccess() {
        DataAccessException exception = null;

        try {
            GameData gameData = sqlGameDAO.getGame(existingGameData.gameID());
            Assertions.assertNotNull(gameData, "Game data not found");
            Assertions.assertEquals(existingGameData, gameData, "Incorrect game data found");
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(7)
    @DisplayName("Game not found")
    public void gameNotFound() {
        DataAccessException exception = null;

        try {
            GameData gameData = sqlGameDAO.getGame(100);
            Assertions.assertNull(gameData, "Unexpected game data found");
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }


    @Test
    @Order(8)
    @DisplayName("List Games Empty")
    public void listGamesEmpty() {
        DataAccessException exception = null;

        GetGamesResult expectedResult = new GetGamesResult(new ArrayList<>());

        try {
            sqlGameDAO.clear();
            GetGamesResult gameDataList = sqlGameDAO.listGames();
            Assertions.assertEquals(expectedResult, gameDataList, "Unexpected games found");
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(9)
    @DisplayName("List Games Success")
    public void listGamesSuccess() {
        DataAccessException exception = null;

        GameData newGame1 = new GameData(100,  "a", "b", "name", new ChessGame());
        GameData newGame2 = new GameData(101,  "c", "d", "name game 2", new ChessGame());
        GameData newGame3 = new GameData(102,  "e", "f", "Name of Game", new ChessGame());

        Collection<GameData> resultCollection = new ArrayList<>();
        resultCollection.add(existingGameData);
        resultCollection.add(newGame1);
        resultCollection.add(newGame2);
        resultCollection.add(newGame3);

        GetGamesResult expectedResult = new GetGamesResult(resultCollection);

        try {
            sqlGameDAO.createGame(newGame1);
            sqlGameDAO.createGame(newGame2);
            sqlGameDAO.createGame(newGame3);

            GetGamesResult gameDataList = sqlGameDAO.listGames();
            Assertions.assertNotNull(gameDataList, "Game data not found");
            Assertions.assertEquals(4, gameDataList.games().size(), "Incorrect number of games found");
            Assertions.assertEquals(expectedResult, gameDataList, "Incorrect game list");
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(10)
    @DisplayName("Update Game Success")
    public void updateSuccess() {
        DataAccessException exception = null;

        GameData newGame = new GameData(100, null, null, "name", new ChessGame());
        GameData updatedGame = new GameData(newGame.gameID(), "WhiteUsername", "BlackUsername", newGame.gameName(), newGame.game());

        try {
            sqlGameDAO.createGame(newGame);
            sqlGameDAO.updateGame(newGame, updatedGame);
            GameData gameDataOut = sqlGameDAO.getGame(newGame.gameID());

            Assertions.assertEquals(gameDataOut, updatedGame, "Game not properly updated");
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(11)
    @DisplayName("Update Game Different ID")
    public void updateDifferentID() {
        DataAccessException exception = null;

        GameData newGame = new GameData(100, null, null, "name", new ChessGame());
        GameData updatedGame = new GameData(101, "WhiteUsername", "BlackUsername", newGame.gameName(), newGame.game());

        try {
            sqlGameDAO.createGame(newGame);
            sqlGameDAO.updateGame(newGame, updatedGame);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(BadRequestException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(12)
    @DisplayName("Update Game White Team Taken")
    public void updateWhiteTeamTaken() {
        DataAccessException exception = null;

        int ID = existingGameData.gameID();
        String gameName = existingGameData.gameName();
        String blackUsername = existingGameData.blackUsername();
        ChessGame game = existingGameData.game();

        GameData updatedGame = new GameData(ID, "WhiteUsername", blackUsername, gameName, game);

        try {
            sqlGameDAO.updateGame(existingGameData, updatedGame);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(AlreadyTakenException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(13)
    @DisplayName("Update Game Black Team Taken")
    public void updateBlackTeamTaken() {
        DataAccessException exception = null;

        int ID = existingGameData.gameID();
        String gameName = existingGameData.gameName();
        String whiteUsername = existingGameData.whiteUsername();
        ChessGame game = existingGameData.game();

        GameData updatedGame = new GameData(ID, whiteUsername, "blackUsername", gameName, game);

        try {
            sqlGameDAO.updateGame(existingGameData, updatedGame);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(AlreadyTakenException.class, exception.getClass(), "Incorrect exception thrown");
    }
}



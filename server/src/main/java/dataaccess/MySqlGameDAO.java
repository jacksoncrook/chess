package dataaccess;

import model.GameData;
import model.GetGamesResult;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO{
    public static final Collection<GameData> AUTH_DATA_TABLE = new ArrayList<>();

    public MySqlGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createGame(GameData gameData) {
        AUTH_DATA_TABLE.add(gameData);
    }

    @Override
    public GameData getGame(int gameID) {
        for (GameData gameData : AUTH_DATA_TABLE) {
            if (gameData.gameID() == gameID) {
                return gameData;
            }
        }
        return null;
    }

    @Override
    public GetGamesResult listGames() {
        return new GetGamesResult(AUTH_DATA_TABLE);
    }

    @Override
    public void updateGame(GameData oldGameData, GameData newGameData) {
        AUTH_DATA_TABLE.remove(oldGameData);
        AUTH_DATA_TABLE.add(newGameData);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE games";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException(String.format("Unable to empty database: %s", ex.getMessage()));
        }
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        String statement =
                """
                CREATE TABLE IF NOT EXISTS games (
                  `gameID` int NOT NULL,
                  `gameName` varchar(256) NOT NULL,
                  `whiteUsername` varchar(256) DEFAULT NULL,
                  `blackUsername` varchar(256) DEFAULT NULL,
                  `game` text NOT NULL,
                  PRIMARY KEY (gameID)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                """;

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}

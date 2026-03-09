package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
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
    public void createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";

        String whiteName = gameData.whiteUsername();
        String blackName = gameData.blackUsername();

        if (whiteName != null && !whiteName.matches("[a-zA-Z0-9-_]+")) {
            throw new BadRequestException("Error: invalid white username");
        }

        if (blackName != null && !blackName.matches("[a-zA-Z0-9-_]+")) {
            throw new BadRequestException("Error: invalid black username");
        }

        if (!gameData.gameName().matches("[a-zA-Z0-9-_!' ]+")) {
            throw new BadRequestException("Error: invalid game name");
        }

        String game = new Gson().toJson(gameData.game());

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameData.gameID());
                preparedStatement.setString(2, gameData.whiteUsername());
                preparedStatement.setString(3, gameData.blackUsername());
                preparedStatement.setString(4, gameData.gameName());
                preparedStatement.setString(5, game);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException(String.format("Unable to add authData to database: %s", ex.getMessage()));
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID = ?";
        int gameIDOut = 0;
        String whiteUsername = null;
        String blackUsername = null;
        String gameName = null;
        String game;
        ChessGame chessGame = null;

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        gameIDOut = rs.getInt("gameID");
                        whiteUsername = rs.getString("whiteUsername");
                        blackUsername = rs.getString("blackUsername");
                        gameName = rs.getString("gameName");
                        game = rs.getString("game");
                        chessGame = new Gson().fromJson(game, ChessGame.class);
                    }

                    if (gameIDOut == gameID) {
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException(String.format("Unable to find gameID in database: %s", ex.getMessage()));
        }
    }

    @Override
    public GetGamesResult listGames() throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";

        Collection<GameData> gameDataOut = new ArrayList<>();

        int gameIDOut = 0;
        String whiteUsername = null;
        String blackUsername = null;
        String gameName = null;
        String game;
        ChessGame chessGame = null;

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        gameIDOut = rs.getInt("gameID");
                        whiteUsername = rs.getString("whiteUsername");
                        blackUsername = rs.getString("blackUsername");
                        gameName = rs.getString("gameName");
                        game = rs.getString("game");
                        chessGame = new Gson().fromJson(game, ChessGame.class);

                        if (gameIDOut != 0) {
                            gameDataOut.add(new GameData(gameIDOut, whiteUsername, blackUsername, gameName, chessGame));
                        }
                    }
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException(String.format("Unable to find gameID in database: %s", ex.getMessage()));
        }
        return new GetGamesResult(gameDataOut);
    }

    @Override
    public void updateGame(GameData oldGameData, GameData newGameData) throws DataAccessException {
        var statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?";
        String game = new Gson().toJson(newGameData.game());

        if (oldGameData.gameID() != newGameData.gameID()) {
            throw new BadRequestException("Error: gameIDs don't match");
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, newGameData.whiteUsername());
                preparedStatement.setString(2, newGameData.blackUsername());
                preparedStatement.setString(3, game);
                preparedStatement.setInt(4, oldGameData.gameID());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException(String.format("Unable to update game in database: %s", ex.getMessage()));
        }
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

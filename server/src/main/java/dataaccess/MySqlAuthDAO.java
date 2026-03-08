package dataaccess;

import model.AuthData;

import java.sql.*;

import java.util.ArrayList;
import java.util.Collection;

public class MySqlAuthDAO implements AuthDAO {
    public static final Collection<AuthData> AUTH_DATA_TABLE = new ArrayList<>();

    public MySqlAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createAuth(AuthData authData) {
        AUTH_DATA_TABLE.add(authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        for (AuthData authData : AUTH_DATA_TABLE) {
            if (authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) {
        AUTH_DATA_TABLE.remove(authData);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auth";

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
            CREATE TABLE IF NOT EXISTS auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (authToken)
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
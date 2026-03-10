package dataaccess;

import model.AuthData;

import java.sql.*;

public class MySqlAuthDAO implements AuthDAO {

    public MySqlAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";

        if (!authData.authToken().matches("[a-fA-F0-9-]+")) {
            throw new UnauthorizedException("Error: invalid authToken");
        }

        if (!authData.username().matches("[a-zA-Z0-9-_]+")) {
            throw new BadRequestException("Error: invalid username");
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.setString(2, authData.username());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DatabaseException(String.format("Error: Unable to add authData to database: %s", ex.getMessage()));
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT authToken, username FROM auth WHERE authToken = ?";
        String authTokenOut = null;
        String username = null;

        if (!authToken.matches("[a-fA-F0-9-]+")) {
            throw new UnauthorizedException("Error: invalid authToken");
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        authTokenOut = rs.getString("authToken");
                        username = rs.getString("username");
                    }

                    if (authTokenOut != null && username != null) {
                        return new AuthData(authTokenOut, username);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DatabaseException(String.format("Error: Unable to find authData in database: %s", ex.getMessage()));
        }
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken = ? AND username = ?";

        if (!authData.authToken().matches("[a-fA-F0-9-]+")) {
            throw new UnauthorizedException("Error: invalid authToken");
        }

        if (!authData.username().matches("[a-zA-Z0-9-_]+")) {
            throw new BadRequestException("Error: invalid username");
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.setString(2, authData.username());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DatabaseException(String.format("Error: Unable to remove authData from database: %s", ex.getMessage()));
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auth";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DatabaseException(String.format("Error: Unable to empty database: %s", ex.getMessage()));
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
            throw new DatabaseException(String.format("Error: Unable to configure database: %s", ex.getMessage()));
        }
    }
}
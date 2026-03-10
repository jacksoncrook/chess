package dataaccess;

import model.LoginRequest;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO {

    public MySqlUserDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO users (username, hashedPassword, email) VALUES (?, ?, ?)";

        if (!userData.username().matches("[a-zA-Z0-9-_]+")) {
            throw new BadRequestException("Error: invalid username");
        }

        if (!userData.email().matches("[a-zA-Z0-9-_@!#$%&'*+-/=?^{|}~.`]+")) {
            throw new BadRequestException("Error: invalid email");
        }

        if (doesUserExist(userData.username())) {
            throw new AlreadyTakenException("Error: username already taken");
        }

        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, userData.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DatabaseException(String.format("Error: Unable to add authData to database: %s", ex.getMessage()));
        }
    }

    @Override
    public boolean doesUserExist(String username) throws DataAccessException {
        var statement = "SELECT username FROM users WHERE username = ?";
        String usernameOut = null;

        if (!username.matches("[a-zA-Z0-9-_]+")) {
            throw new UnauthorizedException("Error: invalid username");
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        usernameOut = rs.getString("username");
                    }
                    return usernameOut != null;
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DatabaseException(String.format("Error: Unable to find authData in database: %s", ex.getMessage()));
        }
    }

    @Override
    public boolean verifyUser(LoginRequest loginRequest) throws DataAccessException {
        var statement = "SELECT username, hashedPassword FROM users WHERE username = ?";
        String hashedPassword = null;

        if (!loginRequest.username().matches("[a-zA-Z0-9-_]+")) {
            throw new UnauthorizedException("Error: invalid username");
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, loginRequest.username());
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        hashedPassword = rs.getString("hashedPassword");
                    }
                    return BCrypt.checkpw(loginRequest.password(), hashedPassword);
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DatabaseException(String.format("Error: Unable to find authData in database: %s", ex.getMessage()));
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE users";

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
                CREATE TABLE IF NOT EXISTS users (
                  `username` varchar(256) NOT NULL,
                  `hashedPassword` varchar(256) NOT NULL,
                  `email` varchar(320),
                  PRIMARY KEY (username)
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

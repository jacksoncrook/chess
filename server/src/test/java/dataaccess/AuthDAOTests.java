package dataaccess;

import model.*;
import org.junit.jupiter.api.*;

import java.util.UUID;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthDAOTests {

    private static AuthDAO sqlAuthDAO;
    private static AuthData existingUser;

    // ### TESTING SETUP/CLEANUP ###

    @BeforeAll
    public static void init() {
        DataAccessException exception = null;
        existingUser = new AuthData(UUID.randomUUID().toString(), "ExistingUser");

        try {
            sqlAuthDAO = new MySqlAuthDAO();
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception);
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        sqlAuthDAO.clear();
        sqlAuthDAO.createAuth(existingUser);
    }

    // ### UNIT TESTS ###

    @Test
    @Order(1)
    @DisplayName("Clear Success")
    public void clearSuccess() {
        DataAccessException exception = null;

        try {
            sqlAuthDAO.clear();
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

        String authToken = UUID.randomUUID().toString();
        String username = "NewUser";

        AuthData newUser = new AuthData(authToken, username);

        try {
            sqlAuthDAO.createAuth(newUser);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(3)
    @DisplayName("Auth Already Exists")
    public void authExists() {
        DataAccessException exception = null;

        try {
            sqlAuthDAO.createAuth(existingUser);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
    }

    @Test
    @Order(4)
    @DisplayName("Creation With Invalid Auth Token")
    public void createInvalidAuthToken() {
        DataAccessException exception = null;

        String authToken = "bad authToken!;";
        String username = "NewUser";

        AuthData newUser = new AuthData(authToken, username);

        try {
            sqlAuthDAO.createAuth(newUser);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(UnauthorizedException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(5)
    @DisplayName("Invalid Username")
    public void invalidUsername() {
        DataAccessException exception = null;

        String authToken = UUID.randomUUID().toString();
        String username = "bad username'; DROP TABLE auth;";

        AuthData newUser = new AuthData(authToken, username);

        try {
            sqlAuthDAO.createAuth(newUser);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(BadRequestException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(6)
    @DisplayName("Get Auth Success")
    public void getAuthSuccess() {
        DataAccessException exception = null;

        try {
            AuthData authData = sqlAuthDAO.getAuth(existingUser.authToken());
            Assertions.assertNotNull(authData, "AuthData not found");
            Assertions.assertEquals(existingUser, authData, "Incorrect authData returned");
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(7)
    @DisplayName("Auth not found")
    public void authNotFound() {
        DataAccessException exception = null;

        String authToken = UUID.randomUUID().toString();

        try {
            AuthData authData = sqlAuthDAO.getAuth(authToken);
            Assertions.assertNull(authData, "AuthData found");
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(8)
    @DisplayName("Delete Auth Success")
    public void deleteAuthSuccess() {
        DataAccessException exception = null;

        try {
            sqlAuthDAO.deleteAuth(existingUser);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(9)
    @DisplayName("Delete Invalid Auth")
    public void deleteInvalidAuth() {
        DataAccessException exception = null;

        String authToken = "Bad AuthToken!';";
        String username = "NewUser";

        AuthData newUser = new AuthData(authToken, username);

        try {
            sqlAuthDAO.deleteAuth(newUser);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(UnauthorizedException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(10)
    @DisplayName("Delete Invalid Username")
    public void deleteInvalidUsername() {
        DataAccessException exception = null;

        String authToken = UUID.randomUUID().toString();
        String username = "bad usernamE+;[]";

        AuthData newUser = new AuthData(authToken, username);

        try {
            sqlAuthDAO.deleteAuth(newUser);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(BadRequestException.class, exception.getClass(), "Incorrect exception thrown");
    }
}
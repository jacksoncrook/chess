package dataaccess;

import model.*;
import org.junit.jupiter.api.*;

import java.util.UUID;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOTests {

    private static UserDAO sqlUserDAO;
    private static UserData existingUser;

    // ### TESTING SETUP/CLEANUP ###

    @BeforeAll
    public static void init() {
        DataAccessException exception = null;
        existingUser = new UserData("ExistingUser", "ExistingPassword", "eu@gmail.com");

        try {
            sqlUserDAO = new MySqlUserDAO();
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception);
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        sqlUserDAO.clear();
        sqlUserDAO.createUser(existingUser);
    }

    // ### UNIT TESTS ###

    @Test
    @Order(1)
    @DisplayName("Clear Success")
    public void clearSuccess() {
        DataAccessException exception = null;

        try {
            sqlUserDAO.clear();
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

        String username = "NewUser";
        String password = "NewPassword";
        String email = "email@gmail";

        UserData newUser = new UserData(username, password, email);

        try {
            sqlUserDAO.createUser(newUser);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(3)
    @DisplayName("User Already Exists")
    public void userExists() {
        DataAccessException exception = null;

        try {
            sqlUserDAO.createUser(existingUser);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(AlreadyTakenException.class, exception, "Incorrect exception type");
    }

    @Test
    @Order(4)
    @DisplayName("Creation With Invalid Username")
    public void createInvalidUsername() {
        DataAccessException exception = null;

        String username = "bad Username !;'[]";
        String password = "newPassword";
        String email = "email@gmail";

        UserData newUser = new UserData(username, password, email);

        try {
            sqlUserDAO.createUser(newUser);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(BadRequestException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(5)
    @DisplayName("Invalid Username")
    public void invalidUsername() {
        DataAccessException exception = null;

        String username = "newUser";
        String password = "bad Password!]; ();?/";
        String email = "email@gmail";

        UserData newUser = new UserData(username, password, email);

        try {
            sqlUserDAO.createUser(newUser);
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNotNull(exception, "Expected exception not thrown");
        Assertions.assertEquals(BadRequestException.class, exception.getClass(), "Incorrect exception thrown");
    }

    @Test
    @Order(6)
    @DisplayName("Get User Success")
    public void getUserSuccess() {
        DataAccessException exception = null;

        try {
            UserData userData = sqlUserDAO.getUser(existingUser.username());
            Assertions.assertNotNull(userData, "UserData not found");
            Assertions.assertEquals(existingUser, userData, "Incorrect user data returned");
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }

    @Test
    @Order(7)
    @DisplayName("User not found")
    public void userNotFound() {
        DataAccessException exception = null;

        String username = "unknownUsername";

        try {
            UserData userData = sqlUserDAO.getUser(username);
            Assertions.assertNull(userData, "Unexpected UserData found");
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }
}
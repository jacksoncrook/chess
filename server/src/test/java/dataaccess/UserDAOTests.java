package dataaccess;

import model.*;
import org.junit.jupiter.api.*;

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
        Assertions.assertEquals(AlreadyTakenException.class, exception.getClass(), "Incorrect exception type");
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
    @DisplayName("Invalid Email")
    public void createInvalidEmail() {
        DataAccessException exception = null;

        String username = "newUser";
        String password = "userPassword";
        String email = "Bad email@gmail[]";

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
    @DisplayName("Find User Success")
    public void findUserSuccess() {
        DataAccessException exception = null;

        try {
            boolean userExists = sqlUserDAO.doesUserExist(existingUser.username());
            Assertions.assertTrue(userExists, "UserData not found");
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
            boolean userExists = sqlUserDAO.doesUserExist(username);
            Assertions.assertFalse(userExists, "Unexpected UserData found");
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }
}
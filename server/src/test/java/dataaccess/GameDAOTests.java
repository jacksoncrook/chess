package dataaccess;

import model.*;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameDAOTests {
    /*private static AuthDAO sqlAuthDAO;

    static {
        try {
            sqlAuthDAO = new MySqlAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    // ### TESTING SETUP/CLEANUP ###

    @BeforeAll
    public static void init() {
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        sqlAuthDAO.clear();
    }*/

    // ### UNIT TESTS ###

    @Test
    @Order(1)
    @DisplayName("Clear Success")
    public void clearSuccess() {
        DataAccessException exception = null;

        try {
            GameDAO sqlDAO = new MySqlGameDAO();
            sqlDAO.clear();
        } catch (DataAccessException e) {
            exception = e;
        }

        Assertions.assertNull(exception, "Unexpected exception thrown");
    }
}


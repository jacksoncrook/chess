package dataaccess;

import model.LoginRequest;
import model.UserData;

public interface UserDAO {
    void createUser(UserData userData) throws DataAccessException;
    boolean doesUserExist(String username) throws DataAccessException;
    boolean verifyUser(LoginRequest loginRequest) throws DataAccessException;
    void clear() throws DataAccessException;
}

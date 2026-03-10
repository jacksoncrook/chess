package dataaccess;

import model.LoginRequest;
import model.UserData;

public interface UserDAO {
    void createUser(UserData userData);
    boolean doesUserExist(String username);
    boolean verifyUser(LoginRequest loginRequest);
    void clear();
}

package dataaccess;

import model.LoginRequest;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserDAO implements UserDAO{
    public static final Collection<UserData> USER_DATA_TABLE = new ArrayList<>();

    @Override
    public void createUser(UserData userData) {
        USER_DATA_TABLE.add(userData);
    }

    @Override
    public boolean doesUserExist(String username) {
        for (UserData userData : USER_DATA_TABLE) {
            if (userData.username().equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean verifyUser(LoginRequest loginRequest) {
        for (UserData userData : USER_DATA_TABLE) {
            if (userData.username().equals(loginRequest.username())) {
                return userData.password().equals(loginRequest.password());
            }
        }
        return false;
    }

    @Override
    public void clear() {
        USER_DATA_TABLE.clear();
    }


}

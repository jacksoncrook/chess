package dataaccess;

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
    public UserData getUser(String username) {
        for (UserData userData : USER_DATA_TABLE) {
            if (userData.username().equals(username)) {
                return userData;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        USER_DATA_TABLE.clear();
    }


}

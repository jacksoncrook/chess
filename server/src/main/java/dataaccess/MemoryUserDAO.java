package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserDAO implements UserDAO{
    private static final Collection<UserData> userDataTable = new ArrayList<>();

    @Override
    public void createUser(UserData userData) {
        userDataTable.add(userData);
    }

    @Override
    public UserData getUser(String username) {
        for (UserData userData : userDataTable) {
            if (userData.username().equals(username)) {
                return userData;
            }
        }
        return null;
    }
}

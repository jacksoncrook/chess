package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryAuthDAO implements AuthDAO {
    public static final Collection<AuthData> authDataTable = new ArrayList<>();

    @Override
    public void createAuth(AuthData authData) {
        authDataTable.add(authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        for (AuthData authData :authDataTable) {
            if (authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) {
        authDataTable.remove(authData);
    }

    @Override
    public void clear() {
        authDataTable.clear();
    }
}
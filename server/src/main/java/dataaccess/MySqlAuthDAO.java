package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

public class MySqlAuthDAO implements AuthDAO {
    public static final Collection<AuthData> AUTH_DATA_TABLE = new ArrayList<>();

    @Override
    public void createAuth(AuthData authData) {
        AUTH_DATA_TABLE.add(authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        for (AuthData authData : AUTH_DATA_TABLE) {
            if (authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) {
        AUTH_DATA_TABLE.remove(authData);
    }

    @Override
    public void clear() {
        AUTH_DATA_TABLE.clear();
    }
}
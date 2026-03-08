package dataaccess;

public interface AuthDAO {
    void createAuth(model.AuthData authData);
    model.AuthData getAuth(String authToken);
    void deleteAuth(model.AuthData authData);
    void clear() throws DataAccessException;
}

package dataaccess;

public interface AuthDAO {
    void createAuth(model.AuthData authData) throws DataAccessException;
    model.AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(model.AuthData authData) throws DataAccessException;
    void clear() throws DataAccessException;
}

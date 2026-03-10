package dataaccess;

public class DatabaseException extends DataAccessException {
    public DatabaseException(String message) {
        super(message);
    }
    public DatabaseException(String message, Throwable ex) {
        super(message, ex);
    }
}

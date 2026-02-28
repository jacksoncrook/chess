package dataaccess;

public interface UserDAO {
    void createUser(model.UserData userData);
    model.UserData getUser(String username);
    void clear();
}

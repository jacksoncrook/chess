package dataaccess;

import model.GameData;
import model.GetGamesResult;

public interface GameDAO {
    void createGame(GameData gameData) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    GetGamesResult listGames() throws DataAccessException;
    void updateGame(GameData oldGameData, GameData newGameData) throws DataAccessException;
    void clear() throws DataAccessException;
}

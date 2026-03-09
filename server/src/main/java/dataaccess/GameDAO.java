package dataaccess;

import model.GameData;
import model.GetGamesResult;

public interface GameDAO {
    void createGame(GameData gameData);
    GameData getGame(int gameID);
    GetGamesResult listGames();
    void updateGame(GameData oldGameData, GameData newGameData);
    void clear() throws DataAccessException;
}

package dataaccess;

import model.GameData;
import model.GetGamesResult;

import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO{
    public static final Collection<GameData> AUTH_DATA_TABLE = new ArrayList<>();

    @Override
    public void createGame(GameData gameData) {
        AUTH_DATA_TABLE.add(gameData);
    }

    @Override
    public GameData getGame(int gameID) {
        for (GameData gameData : AUTH_DATA_TABLE) {
            if (gameData.gameID() == gameID) {
                return gameData;
            }
        }
        return null;
    }

    @Override
    public GetGamesResult listGames() {
        return new GetGamesResult(AUTH_DATA_TABLE);
    }

    @Override
    public void updateGame(GameData oldGameData, GameData newGameData) {
        AUTH_DATA_TABLE.remove(oldGameData);
        AUTH_DATA_TABLE.add(newGameData);
    }

    @Override
    public void clear() {
        AUTH_DATA_TABLE.clear();
    }
}

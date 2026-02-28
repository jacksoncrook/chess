package dataaccess;

import model.GameData;
import model.GetGamesResult;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryGameDAO implements GameDAO{
    public static final Collection<GameData> gameDataTable = new ArrayList<>();

    @Override
    public void createGame(GameData gameData) {
        gameDataTable.add(gameData);
    }

    @Override
    public GameData getGame(int gameID) {
        for (GameData gameData : gameDataTable) {
            if (gameData.gameID() == gameID) {
                return gameData;
            }
        }
        return null;
    }

    @Override
    public GetGamesResult listGames() {
        return new GetGamesResult(gameDataTable);
    }

    @Override
    public void updateGame(GameData oldGameData, GameData newGameData) {
        gameDataTable.remove(oldGameData);
        gameDataTable.add(newGameData);
    }

    @Override
    public void clear() {
        gameDataTable.clear();
    }
}

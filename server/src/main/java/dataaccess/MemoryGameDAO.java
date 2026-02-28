package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryGameDAO implements GameDAO{
    public static Collection<GameData> gameDataTable = new ArrayList<>();

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
    public Collection<GameData> listGames() {
        return gameDataTable;
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

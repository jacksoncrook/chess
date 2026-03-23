package model;

import java.util.Collection;

public record GetGamesResult(Collection<GameData> games) {
    public GameData get(int index) {
        return (GameData) games.toArray()[index];
    }
}

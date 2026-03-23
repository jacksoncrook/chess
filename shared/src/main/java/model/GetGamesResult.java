package model;

import java.util.Collection;

public record GetGamesResult(Collection<GameData> games) {
    public GameData get(int index) {
        try {
            return (GameData) games.toArray()[index];
        } catch (Exception e) {
            throw new IndexOutOfBoundsException("Error: index out of bounds");
        }
    }
}

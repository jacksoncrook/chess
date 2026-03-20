package model;

import java.util.Collection;

public record GetGamesResult(Collection<GameData> games) {
}

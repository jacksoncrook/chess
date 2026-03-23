package ui;

import model.AuthData;

public record ClientResult(Type type, String message, AuthData authData, String gameID, String teamColor) {
    public enum Type {
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY
    }

    public ClientResult(Type type, String message, AuthData authData) {
        this(type, message, authData, null, null);
    }
}

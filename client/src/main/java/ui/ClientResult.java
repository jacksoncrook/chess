package ui;

import model.AuthData;

public record ClientResult(Type type, String message, AuthData authData) {
    public enum Type {
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY
    }
}

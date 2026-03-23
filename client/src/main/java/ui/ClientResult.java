package ui;

public record ClientResult(Type type, String message) {
    public enum Type {
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY
    }
}

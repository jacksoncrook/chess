package model;

public record CreateGameRequest(String authToken, String gameName) {
    public CreateGameRequest addAuth(String newAuthToken) {
        return new CreateGameRequest(newAuthToken, gameName);
    }
}

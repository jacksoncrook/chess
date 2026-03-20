package model;


public record JoinGameRequest(String playerColor, int gameID, String authToken) {
    public JoinGameRequest addAuth(String newAuthToken) {
        return new JoinGameRequest(playerColor, gameID, newAuthToken);
    }
}

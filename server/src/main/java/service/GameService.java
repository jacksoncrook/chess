package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.GetGamesRequest;

import java.util.Collection;

public class GameService {
    public Collection<GameData> listGames(GetGamesRequest getGamesRequest) throws DataAccessException {
        if (getGamesRequest == null) {
            throw new UnauthorizedException("Error: unauthorized");
        } else if (getGamesRequest.authToken() == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        AuthData authData = new MemoryAuthDAO().getAuth(getGamesRequest.authToken());

        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");

        } else {
            return new MemoryGameDAO().listGames();
        }
    }
}

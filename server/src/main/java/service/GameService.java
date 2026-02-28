package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

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

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        if (createGameRequest == null) {
            throw new UnauthorizedException("Error: unauthorized");
        } else if (createGameRequest.authToken() == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        if (createGameRequest.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }

        AuthData authData = new MemoryAuthDAO().getAuth(createGameRequest.authToken());

        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");

        } else {
            int gameID = 1;
            GameData gameData = new GameData(gameID, null, null, createGameRequest.gameName(), new ChessGame());
            new MemoryGameDAO().createGame(gameData);
            return new CreateGameResult(gameID);
        }
    }
}

package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.util.Collection;

public class GameService {
    private static int gameIDCounter = 1000;

    public Collection<GameData> listGames(GetGamesRequest getGamesRequest) throws DataAccessException {
        if (getGamesRequest == null) {
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
        if (createGameRequest.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }

        AuthData authData = new MemoryAuthDAO().getAuth(createGameRequest.authToken());

        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        int gameID = gameIDCounter++;

        GameData gameData = new GameData(gameID, null, null, createGameRequest.gameName(), new ChessGame());
        new MemoryGameDAO().createGame(gameData);

        return new CreateGameResult(gameID);
    }


    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        if (joinGameRequest == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        String color = joinGameRequest.playerColor();

        if (joinGameRequest.gameID() == 0 || color == null) {
            throw new BadRequestException("Error: bad request");
        } else if (!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new BadRequestException("Error: bad request");
        }

        AuthData authData = new MemoryAuthDAO().getAuth(joinGameRequest.authToken());

        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        GameData oldGameData = new MemoryGameDAO().getGame(joinGameRequest.gameID());

        if (joinGameRequest.playerColor().equals("WHITE") && oldGameData.whiteUsername() == null) {
            GameData newGameData = oldGameData.addWhiteUser(authData.username());
            new MemoryGameDAO().updateGame(oldGameData, newGameData);

        } else if (oldGameData.blackUsername() == null) {
            GameData newGameData = oldGameData.addBlackUser(authData.username());
            new MemoryGameDAO().updateGame(oldGameData, newGameData);

        } else {
            throw new AlreadyTakenException("Error: already taken");
        }
    }
}

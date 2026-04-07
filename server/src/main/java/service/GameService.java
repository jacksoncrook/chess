package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.*;
import model.*;

public class GameService {
    private static int gameIDCounter = 1000;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static UserDAO userDAO;

    public GameService(String databaseType) throws DataAccessException {
        if (databaseType != null && databaseType.equals("Memory")) {
            authDAO = new MemoryAuthDAO();
            gameDAO = new MemoryGameDAO();
            userDAO = new MemoryUserDAO();
        } else {
            authDAO = new MySqlAuthDAO();
            gameDAO = new MySqlGameDAO();
            userDAO = new MySqlUserDAO();
        }
    }

    public GetGamesResult listGames(GetGamesRequest getGamesRequest) throws DataAccessException {
        if (getGamesRequest == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        AuthData authData = authDAO.getAuth(getGamesRequest.authToken());

        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");

        } else {
            return gameDAO.listGames();
        }
    }


    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        if (createGameRequest.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }

        AuthData authData = authDAO.getAuth(createGameRequest.authToken());

        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        int gameID = gameIDCounter++;

        while (gameDAO.getGame(gameID) != null) {
            gameID = gameIDCounter++;
        }

        GameData gameData = new GameData(gameID, null, null, createGameRequest.gameName(), new ChessGame());
        gameDAO.createGame(gameData);

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

        AuthData authData = authDAO.getAuth(joinGameRequest.authToken());

        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        GameData oldGameData = gameDAO.getGame(joinGameRequest.gameID());

        if (joinGameRequest.playerColor().equals("WHITE") && oldGameData.whiteUsername() == null) {
            GameData newGameData = oldGameData.addWhiteUser(authData.username());
            gameDAO.updateGame(oldGameData, newGameData);

        } else if (joinGameRequest.playerColor().equals("BLACK") && oldGameData.blackUsername() == null) {
            GameData newGameData = oldGameData.addBlackUser(authData.username());
            gameDAO.updateGame(oldGameData, newGameData);

        } else {
            throw new AlreadyTakenException("Error: already taken");
        }
    }

    public void resignGame(String authToken, int gameID, String color) throws DataAccessException {
        if (authToken == null || color == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        if (gameID == 0) {
            throw new BadRequestException("Error: bad request");
        }

        GameData oldGameData = gameDAO.getGame(gameID);
        String playerUsername;

        if (color.equals("WHITE")) {
            playerUsername = oldGameData.whiteUsername();
        } else if (color.equals("BLACK")) {
            playerUsername = oldGameData.blackUsername();
        } else {
            throw new BadRequestException("Error: bad request");
        }

        AuthData authData = authDAO.getAuth(authToken);

        if (authData == null || !authData.username().equals(playerUsername)) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        ChessGame newGame = oldGameData.game();
        newGame.setTeamTurn(ChessGame.TeamColor.GAME_OVER);
        GameData newGameData = oldGameData.updateGameData(newGame);

        gameDAO.updateGame(oldGameData, newGameData);
    }

    public boolean gameIsOver(int gameID) throws DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        return gameData.game().getTeamTurn() == ChessGame.TeamColor.GAME_OVER;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        GetGamesResult gameList = gameDAO.listGames();
        for (GameData game : gameList.games()) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    public ChessGame makeMove(String authToken, int gameID, ChessMove move) throws DataAccessException, InvalidMoveException {
        if (authToken == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        if (gameID == 0) {
            throw new BadRequestException("Error: bad request");
        }

        AuthData authData = authDAO.getAuth(authToken);

        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        GameData oldGameData = gameDAO.getGame(gameID);
        ChessGame game = oldGameData.game();
        game.makeMove(move);

        if (game.isInCheckmate(game.getTeamTurn()) || game.isInStalemate(game.getTeamTurn())) {
            game.setTeamTurn(ChessGame.TeamColor.GAME_OVER);
        }

        GameData newGameData = oldGameData.updateGameData(game);

        gameDAO.updateGame(oldGameData, newGameData);
        return game;
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
}

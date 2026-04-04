package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import chess.DangerCalculator.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentTeam;
    private ChessBoard board = new ChessBoard();
    private final Collection<ChessPosition> enPassantSpaces = new ArrayList<>();

    public ChessGame() {
        this.currentTeam = TeamColor.WHITE;
        board.resetBoard();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTeam == chessGame.currentTeam && Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeam, getBoard());
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        chess.ChessPiece piece = board.getPiece(startPosition);
        if (piece == null)  {
            return null;
        }
        TeamColor teamColor = piece.getTeamColor();
        Collection<ChessMove> out = new ArrayList<>();

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        if (piece.isType(ChessPiece.PieceType.KING)) {
            for (ChessMove currentMove : moves) {
                if (spaceIsSafeAfterMove(currentMove.getEndPosition(), teamColor, currentMove)) {
                    out.add(currentMove);
                }
            }

            if (piece.getNotMoved() && !isInCheck(teamColor) && (startPosition.getRow() == 1 || startPosition.getRow() == 8)) {
                ChessPosition rookSquare = new ChessPosition(startPosition.getRow(), 8);
                ChessPiece rook = board.getPiece(rookSquare);
                ChessPosition intermediateSquare = new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 1);
                ChessPosition castleSquare = new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 2);
                ChessMove castling = new ChessMove(startPosition, castleSquare, null);

                boolean castlingIsSafe = spaceIsSafe(intermediateSquare, teamColor) && spaceIsSafe(castleSquare, teamColor);
                boolean spaceToCastle = board.getPiece(intermediateSquare) == null && board.getPiece(castleSquare) == null;

                if (castlingIsSafe && spaceToCastle && rook != null && rook.getNotMoved()) {
                    out.add(castling);
                }

                rookSquare = new ChessPosition(startPosition.getRow(), 1);
                rook = board.getPiece(rookSquare);
                intermediateSquare = new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 1);
                castleSquare = new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 2);
                castling = new ChessMove(startPosition, castleSquare, null);

                castlingIsSafe = spaceIsSafe(intermediateSquare, teamColor) && spaceIsSafe(castleSquare, teamColor);
                spaceToCastle = board.getPiece(intermediateSquare) == null && board.getPiece(castleSquare) == null;

                if (castlingIsSafe && spaceToCastle && rook != null && rook.getNotMoved()) {
                    out.add(castling);
                }
            }

        } else {
            ChessPosition kingPosition = findKing(teamColor);

            for (ChessMove currentMove : moves) {
                if (spaceIsSafeAfterMove(kingPosition, teamColor, currentMove)) {
                    out.add(currentMove);
                }
            }
        }


        if (piece.isType(ChessPiece.PieceType.PAWN) && !enPassantSpaces.isEmpty()) {
            ChessMove enPassantMove = enPassant(startPosition, teamColor);
            if (enPassantMove != null) {
                out.add(enPassantMove);
            }
        }

        return out;
    }

    public Collection<ChessPosition> validMoveLocations(ChessPosition startPosition) {
        Collection<ChessPosition> out = new ArrayList<>();

        Collection<ChessMove> moves = validMoves(startPosition);
        for (ChessMove move : moves) {
            out.add(move.getEndPosition());
        }

        return out;
    }

    public Collection<ChessMove> teamValidMoves(TeamColor teamColor) {
        Collection<ChessPosition> piecePositions = getTeamPiecePositions(teamColor);
        Collection<ChessMove> out = new ArrayList<>();
        for (ChessPosition currentPiece : piecePositions) {
            out.addAll(validMoves(currentPiece));
        }
        return out;
    }

    public Collection<ChessPosition> getTeamPiecePositions(TeamColor teamColor) {
        ChessPiece piece;
        ChessPosition position;
        Collection<ChessPosition> out = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                position = new ChessPosition(row, col);
                piece = board.getPiece(position);
                if (piece == null) {
                    continue;
                }
                if (piece.getTeamColor() == teamColor) {
                    out.add(position);
                }
            }
        }
        return out;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = this.board.getPiece(startPosition);
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();

        if (piece == null) {
            throw new InvalidMoveException();
        }

        if (piece.getTeamColor() != this.getTeamTurn()) {
            throw new InvalidMoveException();
        }

        if (validMoves(startPosition).contains(move)) {
            this.board.addPiece(startPosition, null);
            this.board.addPiece(endPosition, piece);
            piece.setMoved(true);

            if (castleCheck(piece, move)) {
                castle(move);
            }

            if (enPassantSpaces.contains(endPosition)) {
                ChessPosition passedSpace = new ChessPosition(startPosition.getRow(), endPosition.getColumn());
                this.board.addPiece(passedSpace, null);
            }

            enPassantCheck(piece, move);

            piece.promotePiece(promotionPiece);
            switchTeams();
        } else {
            throw new InvalidMoveException();
        }
    }

    private void switchTeams() {
        TeamColor currentTurn = this.getTeamTurn();
        if (currentTurn == TeamColor.WHITE) {
            this.setTeamTurn(TeamColor.BLACK);
        } else {
            this.setTeamTurn(TeamColor.WHITE);
        }
    }

    private void enPassantCheck(ChessPiece piece, ChessMove move) {
        enPassantSpaces.clear();

        if (!piece.isType(ChessPiece.PieceType.PAWN)) {
            return;
        }

        int endRow = move.getEndPosition().getRow();
        int startRow = move.getStartPosition().getRow();

        int distance = endRow - startRow;

        if (distance != 2 && distance != -2) {
            return;
        }

        int enPassantRow = (endRow + startRow) / 2;
        ChessPosition enPassantSpace = new ChessPosition(enPassantRow, move.getEndPosition().getColumn());

        enPassantSpaces.add(enPassantSpace);
    }

    private ChessMove enPassant(ChessPosition startPosition, TeamColor teamColor) {
        int teamModifier;
        ChessPosition nextPosition;
        ChessMove enPassant;

        if (teamColor == TeamColor.WHITE) {
            teamModifier = 1;
        } else {
            teamModifier = -1;
        }

        nextPosition = new ChessPosition(startPosition.getRow() + teamModifier, startPosition.getColumn() + 1);
        if (enPassantSpaces.contains(nextPosition)) {
            enPassant = new ChessMove(startPosition, nextPosition, null);
            return enPassant;
        }

        nextPosition = new ChessPosition(startPosition.getRow() + teamModifier, startPosition.getColumn() - 1);
        if (enPassantSpaces.contains(nextPosition)) {
            enPassant = new ChessMove(startPosition, nextPosition, null);
            return enPassant;
        }

        return null;
    }

    private boolean castleCheck(ChessPiece piece, ChessMove move) {
        if (!piece.isType(ChessPiece.PieceType.KING)) {
            return false;
        }
        int distance = move.getStartPosition().getColumn() - move.getEndPosition().getColumn();
        return distance == 2 || distance == -2;
    }

    private void castle(ChessMove move) {
        int col = move.getEndPosition().getColumn();
        if (col == 7) {
            ChessPosition rookPosition = new ChessPosition(move.getEndPosition().getRow(), 8);
            ChessPiece rook = board.getPiece(rookPosition);
            board.addPiece(new ChessPosition(rookPosition.getRow(), 6), rook);
            board.addPiece(rookPosition, null);
            rook.setMoved(true);
        } else if (col == 3) {
            ChessPosition rookPosition = new ChessPosition(move.getEndPosition().getRow(), 1);
            ChessPiece rook = board.getPiece(rookPosition);
            board.addPiece(new ChessPosition(rookPosition.getRow(), 4), rook);
            board.addPiece(rookPosition, null);
            rook.setMoved(true);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        return !spaceIsSafe(kingPosition, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        Collection<ChessMove> teamMoves = teamValidMoves(teamColor);

        return teamMoves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        Collection<ChessMove> teamMoves = teamValidMoves(teamColor);

        return teamMoves.isEmpty();
    }

    public ChessPosition findKing(TeamColor teamColor) {
        ChessPiece piece;
        ChessPosition position;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                position = new ChessPosition(row, col);
                piece = board.getPiece(position);
                if (piece == null) {
                    continue;
                }

                if (piece.isType(ChessPiece.PieceType.KING) && piece.getTeamColor() == teamColor) {
                    return position;
                }
            }
        }
        return null;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    public boolean spaceIsSafe(ChessPosition position, TeamColor teamColor) {
        if (position == null) {
            return true;
        }

        DangerCalculator dangerCalculator = new DangerCalculator(board);

        boolean pawnDanger = dangerCalculator.dangerFromPawn(position, teamColor, null);
        boolean kingDanger = dangerCalculator.dangerFromKing(position, teamColor, null);
        boolean knightDanger = dangerCalculator.dangerFromKnight(position, teamColor, null);
        boolean bishopDanger = dangerCalculator.dangerFromBishop(position, teamColor, null);
        boolean rookDanger = dangerCalculator.dangerFromRook(position, teamColor, null);

        return !(pawnDanger || kingDanger || knightDanger || bishopDanger || rookDanger);
    }

    public boolean spaceIsSafeAfterMove(ChessPosition position, TeamColor teamColor, ChessMove move) {
        if (position == null) {
            return true;
        }

        DangerCalculator dangerCalculator = new DangerCalculator(board);

        boolean pawnDanger = dangerCalculator.dangerFromPawn(position, teamColor, move);
        boolean kingDanger = dangerCalculator.dangerFromKing(position, teamColor, move);
        boolean knightDanger = dangerCalculator.dangerFromKnight(position, teamColor, move);
        boolean bishopDanger = dangerCalculator.dangerFromBishop(position, teamColor, move);
        boolean rookDanger = dangerCalculator.dangerFromRook(position, teamColor, move);

        return !(pawnDanger || kingDanger || knightDanger || bishopDanger || rookDanger);
    }
}

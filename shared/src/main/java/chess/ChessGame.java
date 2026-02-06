package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
        if (piece == null) return null;

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);



        if (piece.isType(ChessPiece.PieceType.PAWN) && !enPassantSpaces.isEmpty()) {
            int teamModifier;
            ChessPosition nextPosition;
            ChessMove enPassant;

            if (piece.getTeamColor() == TeamColor.WHITE) {
                teamModifier = 1;
            } else {
                teamModifier = -1;
            }

            nextPosition = new ChessPosition(startPosition.getRow() + teamModifier, startPosition.getColumn() + 1);

            if (enPassantSpaces.contains(nextPosition)) {
                enPassant = new ChessMove(startPosition, nextPosition, null);
                moves.add(enPassant);
            }

            nextPosition = new ChessPosition(startPosition.getRow() + teamModifier, startPosition.getColumn() - 1);
            if (enPassantSpaces.contains(nextPosition)) {
                enPassant = new ChessMove(startPosition, nextPosition, null);
                moves.add(enPassant);
            }
        }

        return moves;
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

        if (piece == null) throw new InvalidMoveException();

        if (piece.getTeamColor() != this.getTeamTurn()) throw new InvalidMoveException();

        if (validMoves(startPosition).contains(move)) {
            this.board.addPiece(startPosition, null);
            this.board.addPiece(endPosition, piece);
            piece.setMoved(true);

            if (enPassantSpaces.contains(endPosition)) {
                ChessPosition passedSpace = new ChessPosition(startPosition.getRow(), endPosition.getColumn());
                this.board.addPiece(passedSpace, null);
            }

            enPassantCheck(piece, move);

            piece.promotePiece(promotionPiece);
            switchTeams();
        } else throw new InvalidMoveException();
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

        if (!piece.isType(ChessPiece.PieceType.PAWN)) return;

        int endRow = move.getEndPosition().getRow();
        int startRow = move.getStartPosition().getRow();

        int distance = endRow - startRow;

        if (distance != 2 && distance != -2) return;

        int enPassantRow = (endRow + startRow) / 2;
        ChessPosition enPassantSpace = new ChessPosition(enPassantRow, move.getEndPosition().getColumn());

        enPassantSpaces.add(enPassantSpace);
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
        if (!isInCheck(teamColor)) return false;

        ChessPosition kingPosition = findKing(teamColor);
        Collection<ChessMove> kingMoves = validMoves(kingPosition);

        return kingMoves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) return false;

        ChessPosition kingPosition = findKing(teamColor);
        if (kingPosition == null) return false;
        Collection<ChessMove> kingMoves = validMoves(kingPosition);

        return kingMoves.isEmpty();
    }

    public ChessPosition findKing(TeamColor teamColor) {
        ChessPiece piece;
        ChessPosition position;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                position = new ChessPosition(row, col);
                piece = board.getPiece(position);
                if (piece == null) continue;
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
        if (position == null) return true;

        boolean pawnDanger = dangerFromPawn(position, teamColor);
        boolean kingDanger = dangerFromKing(position, teamColor);
        boolean knightDanger = dangerFromKnight(position, teamColor);
        boolean bishopDanger = dangerFromBishop(position, teamColor);
        boolean rookDanger = dangerFromRook(position, teamColor);

        return !(pawnDanger || kingDanger || knightDanger || bishopDanger || rookDanger);
    }

    private boolean dangerFromRook(ChessPosition position, TeamColor teamColor) {
        int col = position.getColumn();
        int row = position.getRow();
        ChessPiece assailant;
        ChessPosition nextPosition;

        int modifier;
        for (int direction = 1; direction <= 4; direction++) { // Check for Rook/Queen attacks
            modifier = 1;
            do {
                switch (direction) {
                    case 1 -> nextPosition = new ChessPosition(row + modifier, col);
                    case 2 -> nextPosition = new ChessPosition(row - modifier, col);
                    case 3 -> nextPosition = new ChessPosition(row, col + modifier);
                    case 4 -> nextPosition = new ChessPosition(row, col - modifier);
                    default -> nextPosition = position;
                }
                modifier++;
                assailant = board.getPiece(nextPosition);
            } while (nextPosition.isValidPosition() && assailant == null);
            if (assailant != null && ( assailant.isType(ChessPiece.PieceType.QUEEN) || assailant.isType(ChessPiece.PieceType.ROOK) ) && assailant.getTeamColor() != teamColor ) return true;
        }

        return false;
    }

    private boolean dangerFromBishop(ChessPosition position, TeamColor teamColor) {
        int col = position.getColumn();
        int row = position.getRow();
        ChessPiece assailant;
        ChessPosition nextPosition;

        int modifier;
        for (int direction = 1; direction <= 4; direction++) { // Check for Bishop/Queen attacks
            modifier = 1;
            do {
                switch (direction) {
                    case 1 -> nextPosition = new ChessPosition(row + modifier, col + modifier);
                    case 2 -> nextPosition = new ChessPosition(row + modifier, col - modifier);
                    case 3 -> nextPosition = new ChessPosition(row - modifier, col + modifier);
                    case 4 -> nextPosition = new ChessPosition(row - modifier, col - modifier);
                    default -> nextPosition = position;
                }
                modifier++;
                assailant = board.getPiece(nextPosition);
            } while (nextPosition.isValidPosition() && assailant == null);
            if (assailant != null && ( assailant.isType(ChessPiece.PieceType.QUEEN) || assailant.isType(ChessPiece.PieceType.BISHOP) ) && assailant.getTeamColor() != teamColor ) return true;
        }

        return false;
    }

    private boolean dangerFromKnight(ChessPosition position, TeamColor teamColor) {
        int col = position.getColumn();
        int row = position.getRow();
        ChessPiece assailant;
        ChessPosition nextPosition;

        for (int rowDirectionModifier = -1; rowDirectionModifier <= 1; rowDirectionModifier += 2) { // Check for knight attacks
            for (int colDirectionModifier = -1; colDirectionModifier <= 1; colDirectionModifier += 2) {
                nextPosition = new ChessPosition(row + (2 * rowDirectionModifier), col + colDirectionModifier);
                assailant = board.getPiece(nextPosition); // Two spaces vertically, one space horizontally
                if (assailant != null && assailant.getTeamColor() != teamColor && assailant.isType(ChessPiece.PieceType.KNIGHT)) {
                    return true;
                }

                nextPosition = new ChessPosition(row + rowDirectionModifier, col + (2 * colDirectionModifier));
                assailant = board.getPiece(nextPosition); // One space vertically, two spaces horizontally
                if (assailant != null && assailant.getTeamColor() != teamColor && assailant.isType(ChessPiece.PieceType.KNIGHT)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean dangerFromPawn(ChessPosition position, TeamColor teamColor) {
        int col = position.getColumn();
        int row = position.getRow();
        int nextCol;
        int nextRow;
        ChessPiece assailant;
        ChessPosition nextPosition;

        int teamModifier = 0;

        if (teamColor == TeamColor.WHITE) { // Set team modifier for pawn attacks
            teamModifier = 1;
        } else if (teamColor == TeamColor.BLACK) {
            teamModifier = -1;
        }


        for (int colModifier = -1; colModifier <= 1; colModifier += 1) { // Check for pawn attacks
            nextCol = col + colModifier;
            nextRow = row + teamModifier;
            nextPosition = new ChessPosition(nextRow, nextCol);
            assailant = board.getPiece(nextPosition);

            if (assailant != null && assailant.getTeamColor() != teamColor && assailant.isType(ChessPiece.PieceType.PAWN)) {
                return true;
            }
        }

        return false;
    }

    private boolean dangerFromKing(ChessPosition position, TeamColor teamColor) {
        int col = position.getColumn();
        int row = position.getRow();
        int nextCol;
        int nextRow;
        ChessPiece assailant;
        ChessPosition nextPosition;

        for (int colModifier = -1; colModifier <= 1; colModifier++) { // Check for King attacking
            for (int rowModifier = -1; rowModifier <= 1; rowModifier++) {
                nextCol = col + colModifier;
                nextRow = row + rowModifier;
                nextPosition = new ChessPosition(nextRow, nextCol);
                if (nextPosition.isValidPosition()) {
                    assailant = board.getPiece(nextPosition);
                    if (assailant == null) continue;
                    if (assailant.getTeamColor() != teamColor && assailant.isType(ChessPiece.PieceType.KING)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}

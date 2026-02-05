package chess;

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
        return board.getPiece(startPosition).pieceMoves(board, startPosition);
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
        TeamColor currentTurn = this.getTeamTurn();

        if (piece == null) throw new InvalidMoveException();

        if (piece.getTeamColor() != currentTurn) throw new InvalidMoveException();

        if (validMoves(startPosition).contains(move)) {
            this.board.addPiece(startPosition, null);
            this.board.addPiece(endPosition, piece);

            if (promotionPiece != null) piece.promotePiece(promotionPiece);

            if (currentTurn == TeamColor.WHITE) {
                this.setTeamTurn(TeamColor.BLACK);
            } else {
                this.setTeamTurn(TeamColor.WHITE);
            }
        } else throw new InvalidMoveException();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        return spaceIsSafe(kingPosition, teamColor);
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
                if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
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
        int col = position.getColumn();
        int row = position.getRow();
        int nextCol;
        int nextRow;
        int teamModifier = 0;
        ChessPiece assailant;
        ChessPosition nextPosition;

        for (int colModifier = -1; colModifier <= 1; colModifier++) { // Check for King attacking
            for (int rowModifier = -1; rowModifier <= 1; rowModifier++) {
                nextCol = col + colModifier;
                nextRow = row + rowModifier;
                nextPosition = new ChessPosition(nextRow, nextCol);
                if (nextPosition.isValidPosition()) {
                    assailant = board.getPiece(nextPosition);
                    if (assailant.getTeamColor() != teamColor && assailant.getPieceType() == ChessPiece.PieceType.KING) {
                        return false;
                    }
                }
            }
        }

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

            if (assailant != null && assailant.getTeamColor() != teamColor && assailant.getPieceType() == ChessPiece.PieceType.PAWN) {
                return false;
            }
        }

        for (int rowDirectionModifier = -1; rowDirectionModifier <= 1; rowDirectionModifier += 2) { // Check for knight attacks
            for (int colDirectionModifier = -1; colDirectionModifier <= 1; colDirectionModifier += 2) {
                nextPosition = new ChessPosition(row + (2 * rowDirectionModifier), col + colDirectionModifier);
                assailant = board.getPiece(nextPosition); // Two spaces vertically, one space horizontally
                if (assailant != null && assailant.getTeamColor() != teamColor && assailant.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                    return false;
                }

                nextPosition = new ChessPosition(row + rowDirectionModifier, col + (2 * colDirectionModifier));
                assailant = board.getPiece(nextPosition); // One space vertically, two spaces horizontally
                if (assailant != null && assailant.getTeamColor() != teamColor && assailant.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                    return false;
                }
            }
        }

        return true;
    }
}

package chess;

public class DangerCalculator {

    private final ChessBoard board;

    public DangerCalculator(ChessBoard board) {
        this.board = board;
    }
    public boolean dangerFromRook(ChessPosition position, ChessGame.TeamColor teamColor, ChessMove move) {
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
                assailant = board.getPiece(nextPosition, move);
            } while (nextPosition.isValidPosition() && assailant == null);

            boolean assailantNotNull = assailant != null;
            boolean assailantIsQueen = assailantNotNull && assailant.isType(ChessPiece.PieceType.QUEEN);
            boolean assailantIsRook = assailantNotNull && assailant.isType(ChessPiece.PieceType.ROOK);

            if ((assailantIsQueen || assailantIsRook) && assailant.getTeamColor() != teamColor) {
                return true;
            }

        }

        return false;
    }

    public boolean dangerFromBishop(ChessPosition position, ChessGame.TeamColor teamColor, ChessMove move) {
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
                assailant = board.getPiece(nextPosition, move);
            } while (nextPosition.isValidPosition() && assailant == null);

            boolean assailantNotNull = assailant != null;
            boolean assailantIsQueen = assailantNotNull && assailant.isType(ChessPiece.PieceType.QUEEN) ;
            boolean assailantIsBishop = assailantNotNull && assailant.isType(ChessPiece.PieceType.BISHOP);
            if (( assailantIsQueen || assailantIsBishop ) && assailant.getTeamColor() != teamColor ) {
                return true;
            }
        }

        return false;
    }

    public boolean dangerFromKnight(ChessPosition position, ChessGame.TeamColor teamColor, ChessMove move) {
        int col = position.getColumn();
        int row = position.getRow();
        ChessPiece assailant;
        ChessPosition nextPosition;

        for (int rowDirectionModifier = -1; rowDirectionModifier <= 1; rowDirectionModifier += 2) { // Check for knight attacks
            for (int colDirectionModifier = -1; colDirectionModifier <= 1; colDirectionModifier += 2) {
                nextPosition = new ChessPosition(row + (2 * rowDirectionModifier), col + colDirectionModifier);
                assailant = board.getPiece(nextPosition, move); // Two spaces vertically, one space horizontally
                if (assailant != null && assailant.getTeamColor() != teamColor && assailant.isType(ChessPiece.PieceType.KNIGHT)) {
                    return true;
                }

                nextPosition = new ChessPosition(row + rowDirectionModifier, col + (2 * colDirectionModifier));
                assailant = board.getPiece(nextPosition, move); // One space vertically, two spaces horizontally
                if (assailant != null && assailant.getTeamColor() != teamColor && assailant.isType(ChessPiece.PieceType.KNIGHT)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean dangerFromPawn(ChessPosition position, ChessGame.TeamColor teamColor, ChessMove move) {
        int col = position.getColumn();
        int row = position.getRow();
        int nextCol;
        int nextRow;
        ChessPiece assailant;
        ChessPosition nextPosition;

        int teamModifier = 0;

        if (teamColor == ChessGame.TeamColor.WHITE) { // Set team modifier for pawn attacks
            teamModifier = 1;
        } else if (teamColor == ChessGame.TeamColor.BLACK) {
            teamModifier = -1;
        }


        for (int colModifier = -1; colModifier <= 1; colModifier += 1) { // Check for pawn attacks
            nextCol = col + colModifier;
            nextRow = row + teamModifier;
            nextPosition = new ChessPosition(nextRow, nextCol);
            assailant = board.getPiece(nextPosition, move);

            if (assailant != null && assailant.getTeamColor() != teamColor && assailant.isType(ChessPiece.PieceType.PAWN)) {
                return true;
            }
        }

        return false;
    }

    public boolean dangerFromKing(ChessPosition position, ChessGame.TeamColor teamColor, ChessMove move) {
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
                    assailant = board.getPiece(nextPosition, move);
                    if (assailant == null) {
                        continue;
                    }
                    if (assailant.getTeamColor() != teamColor && assailant.isType(ChessPiece.PieceType.KING)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

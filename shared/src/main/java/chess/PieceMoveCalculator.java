package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMoveCalculator {
    private final Collection<ChessMove> out = new ArrayList<>();

    public PieceMoveCalculator() {
    }

    public Collection<ChessMove> moveCalculator(ChessPiece piece, ChessBoard board, ChessPosition myPosition) {
        chess.ChessPiece.PieceType pieceType = piece.getPieceType();
        switch (pieceType) {
            case KING -> {
                return kingMoves(board, myPosition, piece);
            }
            case QUEEN -> {
                return queenMoves(board, myPosition, piece);
            }
            case BISHOP -> {
                return bishopMoves(board, myPosition, piece);
            }
            case KNIGHT -> {
                return knightMoves(board, myPosition, piece);
            }
            case ROOK -> {
                return rookMoves(board, myPosition, piece);
            }
            case PAWN -> {
                return pawnMoves(board, myPosition, piece);
            }
        }
        return null;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int teamMultiplier;
        switch (piece.getTeamColor()) {
            case WHITE -> {
                teamMultiplier = 1;
                if(myPosition.getRow() == 2) {
                    piece.setMoved(false);
                }
            }
            case BLACK -> {
                teamMultiplier = -1;
                if(myPosition.getRow() == 7) {
                    piece.setMoved(false);
                }
            }
            case null, default -> teamMultiplier = 0;
        }
        ChessPosition nextPosition;
        ChessPosition intermediatePosition;
        ChessMove currentMove;
        ChessPiece victim;
        ChessPiece.PieceType promotionPiece = null;
        int numLoops = 1;
        if (teamMultiplier == 1 && myPosition.getRow() == 7) {
            numLoops = 4;
        } else if (teamMultiplier == -1 && myPosition.getRow() == 2) {
            numLoops = 4;
        }

        for(int i = 0; i < numLoops; i++) {
            if (numLoops == 4) {
                switch(i) {
                    case 0 -> promotionPiece = ChessPiece.PieceType.KNIGHT;
                    case 1 -> promotionPiece = ChessPiece.PieceType.BISHOP;
                    case 2 -> promotionPiece = ChessPiece.PieceType.ROOK;
                    case 3 -> promotionPiece = ChessPiece.PieceType.QUEEN;
                }
            }
            nextPosition = new ChessPosition(myPosition.getRow() + teamMultiplier, myPosition.getColumn());
            if (board.getPiece(nextPosition) == null) {
                currentMove = new ChessMove(myPosition, nextPosition, promotionPiece);
                out.add(currentMove);
            }

            intermediatePosition = nextPosition;
            nextPosition = new ChessPosition(myPosition.getRow() + (2 * teamMultiplier), myPosition.getColumn());
            if (!piece.getMoved() && board.getPiece(intermediatePosition) == null && board.getPiece(nextPosition) == null) {
                currentMove = new ChessMove(myPosition, nextPosition, promotionPiece);
                out.add(currentMove);
            }

            if (myPosition.getColumn() < 8) {
                nextPosition = new ChessPosition(myPosition.getRow() + teamMultiplier, myPosition.getColumn() + 1);
                victim = board.getPiece(nextPosition);
                if (victim != null && victim.getTeamColor() != piece.getTeamColor()) {
                    currentMove = new ChessMove(myPosition, nextPosition, promotionPiece);
                    out.add(currentMove);
                }
            }

            if (myPosition.getColumn() > 1) {
                nextPosition = new ChessPosition(myPosition.getRow() + teamMultiplier, myPosition.getColumn() - 1);
                victim = board.getPiece(nextPosition);
                if (victim != null && victim.getTeamColor() != piece.getTeamColor()) {
                    currentMove = new ChessMove(myPosition, nextPosition, promotionPiece);
                    out.add(currentMove);
                }
            }
        }

        return out;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ChessPosition nextPosition;
        for (int rowMod = -1; rowMod <= 1; rowMod++) {
            for (int colMod = -1; colMod <= 1; colMod++) {
                nextPosition = new ChessPosition(rowMod + myRow, colMod + myCol);
                if (nextPosition != myPosition) {
                    validateMove(board, myPosition, nextPosition, piece);
                }
            }
        }
        return out;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ChessPosition nextPosition;
        boolean cont;

        for (int directionModifier = -1; directionModifier <= 1; directionModifier += 2) {
            cont = true;
            for (int nextRow = myRow + directionModifier; cont; nextRow += directionModifier) { // Up and down
                nextPosition = new ChessPosition(nextRow, myCol);
                cont = validateMove(board, myPosition, nextPosition, piece);
            }

            cont = true;
            for (int nextCol = myCol + directionModifier; cont; nextCol += directionModifier) { // Left and right
                nextPosition = new ChessPosition(myRow, nextCol);
                cont = validateMove(board, myPosition, nextPosition, piece);
            }
        }

        return out;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ChessPosition nextPosition;

        boolean cont = true;
        int nextRow = myRow - 1;
        int nextCol = myCol - 1;
        while (cont) { // Down left
            nextPosition = new ChessPosition(nextRow, nextCol);
            cont = validateMove(board, myPosition, nextPosition, piece);
            nextRow--;
            nextCol--;
        }

        cont = true;
        nextRow = myRow + 1;
        nextCol = myCol - 1;
        while (cont) { // Down Right
            nextPosition = new ChessPosition(nextRow, nextCol);
            cont = validateMove(board, myPosition, nextPosition, piece);
            nextRow++;
            nextCol--;
        }

        cont = true;
        nextRow = myRow - 1;
        nextCol = myCol + 1;
        while (cont) { // Up Left
            nextPosition = new ChessPosition(nextRow, nextCol);
            cont = validateMove(board, myPosition, nextPosition, piece);
            nextRow--;
            nextCol++;
        }

        cont = true;
        nextRow = myRow + 1;
        nextCol = myCol + 1;
        while (cont) { // Up Right
            nextPosition = new ChessPosition(nextRow, nextCol);
            cont = validateMove(board, myPosition, nextPosition, piece);
            nextRow++;
            nextCol++;
        }

        return out;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ChessPosition nextPosition;

        for (int rowDirectionModifier = -1; rowDirectionModifier <= 1; rowDirectionModifier += 2) {
            for (int colDirectionModifier = -1; colDirectionModifier <= 1; colDirectionModifier += 2) {
                nextPosition = new ChessPosition(myRow + (2 * rowDirectionModifier), myCol + colDirectionModifier);
                validateMove(board, myPosition, nextPosition, piece); // Two spaces vertically, one space horizontally,
                nextPosition = new ChessPosition(myRow + rowDirectionModifier, myCol + (2 * colDirectionModifier));
                validateMove(board, myPosition, nextPosition, piece); // One space vertically, two spaces horizontally
            }
        }

        return out;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        rookMoves(board, myPosition, piece);
        bishopMoves(board, myPosition, piece);
        return out;
    }

    private boolean validateMove(ChessBoard board, ChessPosition myPosition, ChessPosition nextPosition, ChessPiece piece) {
        if (nextPosition.getColumn() < 1 || nextPosition.getColumn() > 8 || nextPosition.getRow() < 1 || nextPosition.getRow() > 8) {
            return false;
        }

        ChessMove currentMove = new ChessMove(myPosition, nextPosition, null);
        ChessPiece victim = board.getPiece(nextPosition);
        if (victim != null && victim.getTeamColor() == piece.getTeamColor()) {
            return false;
        } else if (victim != null) {
            out.add(currentMove);
            return false;
        }
        out.add(currentMove);
        return true;
    }
}

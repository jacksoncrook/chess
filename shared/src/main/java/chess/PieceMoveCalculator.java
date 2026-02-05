package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMoveCalculator {
    private final Collection<ChessMove> out = new ArrayList<>();
    private final ChessPiece piece;
    private final ChessBoard board;
    private final ChessPosition myPosition;


    public PieceMoveCalculator(ChessPiece piece, ChessBoard board, ChessPosition myPosition) {
        this.piece = piece;
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> moveCalculator() {
        chess.ChessPiece.PieceType pieceType = piece.getPieceType();
        switch (pieceType) {
            case KING -> {
                return kingMoves();
            }
            case QUEEN -> {
                return queenMoves();
            }
            case BISHOP -> {
                return bishopMoves();
            }
            case KNIGHT -> {
                return knightMoves();
            }
            case ROOK -> {
                return rookMoves();
            }
            case PAWN -> {
                return pawnMoves();
            }
        }
        return null;
    }

    private Collection<ChessMove> pawnMoves() {
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

    private Collection<ChessMove> kingMoves() {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ChessPosition nextPosition;
        for (int rowMod = -1; rowMod <= 1; rowMod++) {
            for (int colMod = -1; colMod <= 1; colMod++) {
                nextPosition = new ChessPosition(rowMod + myRow, colMod + myCol);
                if (nextPosition != myPosition) {
                    validateMove(nextPosition);
                }
            }
        }
        return out;
    }

    private Collection<ChessMove> rookMoves() {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ChessPosition nextPosition;
        boolean cont;

        for (int directionModifier = -1; directionModifier <= 1; directionModifier += 2) {
            cont = true;
            for (int nextRow = myRow + directionModifier; cont; nextRow += directionModifier) { // Up and down
                nextPosition = new ChessPosition(nextRow, myCol);
                cont = validateMove(nextPosition);
            }

            cont = true;
            for (int nextCol = myCol + directionModifier; cont; nextCol += directionModifier) { // Left and right
                nextPosition = new ChessPosition(myRow, nextCol);
                cont = validateMove(nextPosition);
            }
        }

        return out;
    }

    private Collection<ChessMove> bishopMoves() {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ChessPosition nextPosition;

        boolean cont = true;
        int nextRow = myRow - 1;
        int nextCol = myCol - 1;
        while (cont) { // Down left
            nextPosition = new ChessPosition(nextRow, nextCol);
            cont = validateMove(nextPosition);
            nextRow--;
            nextCol--;
        }

        cont = true;
        nextRow = myRow + 1;
        nextCol = myCol - 1;
        while (cont) { // Down Right
            nextPosition = new ChessPosition(nextRow, nextCol);
            cont = validateMove(nextPosition);
            nextRow++;
            nextCol--;
        }

        cont = true;
        nextRow = myRow - 1;
        nextCol = myCol + 1;
        while (cont) { // Up Left
            nextPosition = new ChessPosition(nextRow, nextCol);
            cont = validateMove(nextPosition);
            nextRow--;
            nextCol++;
        }

        cont = true;
        nextRow = myRow + 1;
        nextCol = myCol + 1;
        while (cont) { // Up Right
            nextPosition = new ChessPosition(nextRow, nextCol);
            cont = validateMove(nextPosition);
            nextRow++;
            nextCol++;
        }

        return out;
    }

    private Collection<ChessMove> knightMoves() {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ChessPosition nextPosition;

        for (int rowDirectionModifier = -1; rowDirectionModifier <= 1; rowDirectionModifier += 2) {
            for (int colDirectionModifier = -1; colDirectionModifier <= 1; colDirectionModifier += 2) {
                nextPosition = new ChessPosition(myRow + (2 * rowDirectionModifier), myCol + colDirectionModifier);
                validateMove(nextPosition); // Two spaces vertically, one space horizontally,
                nextPosition = new ChessPosition(myRow + rowDirectionModifier, myCol + (2 * colDirectionModifier));
                validateMove(nextPosition); // One space vertically, two spaces horizontally
            }
        }

        return out;
    }

    private Collection<ChessMove> queenMoves() {
        rookMoves();
        bishopMoves();
        return out;
    }

    private boolean validateMove(ChessPosition nextPosition) {
        if (!nextPosition.isValidPosition()) {
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

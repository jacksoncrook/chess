package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMoveCalculator {
    private Collection<ChessMove> out = new ArrayList<>();

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
            case null, default -> {
                teamMultiplier = 0;
            }
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

        return out;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        return out;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        return out;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        return out;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        return out;
    }
}

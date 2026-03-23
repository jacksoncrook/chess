package client;

import chess.*;
import ui.ClientUI;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        ClientUI clientUI = new ClientUI("http://localhost:8080");
        clientUI.run();
    }
}

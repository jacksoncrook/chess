package client;

import chess.*;
import ui.ClientUI;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client ♛");
        ClientUI clientUI = new ClientUI("localhost:8080");
        clientUI.run();
    }
}

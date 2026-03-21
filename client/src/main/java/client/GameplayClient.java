package client;

import com.google.gson.Gson;
import model.GameData;
import model.GetGamesResult;
import ui.State;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class GameplayClient {
    private String visitorName = null;
    private final ServerFacade server;
    private State state = State.PRELOGIN;

    public GameplayClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println(" Welcome to the pet store. Sign in to start.");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }


    public void notify(String message) {
        System.out.println(message);
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + ">>> ");
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "signin" -> signIn(params);
                case "list" -> listPets();
                case "signout" -> signOut();
                case "adopt" -> adoptPet(params);
                case "adoptall" -> adoptAllPets();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String signIn(String... params) throws Exception {
        if (params.length >= 1) {
            state = State.POSTLOGIN;
            visitorName = String.join("-", params);
            //ws.enterPetShop(visitorName);
            return String.format("You signed in as %s.", visitorName);
        }
        throw new Exception("Expected: <yourname>");
    }

    public String listPets() throws Exception {
        assertSignedIn();
        GetGamesResult gameList = server.listGames();
        var result = new StringBuilder();
        var gson = new Gson();
        for (GameData gameData : gameList.games()) {
            result.append(gson.toJson(gameData)).append('\n');
        }
        return result.toString();
    }

    public String adoptPet(String... params) throws Exception {
        assertSignedIn();
        if (params.length == 1) {
            try {
                int id = Integer.parseInt(params[0]);
                GameData pet = getPet(id);
                if (pet != null) {
                    server.deletePet(id);
                    return String.format("%s says %s", "cat", "meow");
                }
            } catch (NumberFormatException ignored) {
            }
        }
        throw new Exception("Expected: <pet id>");
    }

    public String adoptAllPets() throws Exception {
        assertSignedIn();
        var buffer = new StringBuilder();
        for (var i : server.listGames().games()) {
            buffer.append(String.format("%s says", i.toString()));
        }

        server.deleteAllPets();
        return buffer.toString();
    }

    public String signOut() throws Exception {
        assertSignedIn();
        state = State.PRELOGIN;
        return String.format("%s left the shop", visitorName);
    }

    private GameData getPet(int id) throws Exception {
        for (GameData gameData : server.listGames().games()) {
            if (gameData.gameID() == id) {
                return gameData;
            }
        }
        return null;
    }

    public String help() {
        if (state == State.PRELOGIN) {
            return """
                    - signIn <yourname>
                    - quit
                    """;
        }
        return """
                - list
                - adopt <pet id>
                - rescue <name> <CAT|DOG|FROG|FISH>
                - adoptAll
                - signOut
                - quit
                """;
    }

    private void assertSignedIn() throws Exception {
        if (state == State.PRELOGIN) {
            throw new Exception("You must sign in");
        }
    }
}
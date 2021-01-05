package de.htw.battleship;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Requires Java 11 or higher.
 */
public class BattleshipApplication {

    private BattleshipGame game;
    private final Path saveFilePath = Path.of("battleship.save");

    public static void main(String[] args) {
        BattleshipApplication battleshipApplication = new BattleshipApplication();
        battleshipApplication.mainMenu();
    }

    /**
     * Prints a menu to the user, lets them choose an option and executes the chosen option
     */
    private void mainMenu() {

        // TODO print main menu to the console. let user select an option. (s. Aufgabe 3)

        int option;
        while (true) {
            printMenu();
            String input = new Scanner(System.in).nextLine();
            try {
                option = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                wrongInput();
                continue;
            }
            if (option == 1) {
                startNewGame();
            } else if (option == 2) {
                break;
            } else {
                wrongInput();
                continue;
            }
        }

        while (hasRunningGame()) {
            printMenu();
            String input = new Scanner(System.in).nextLine();
            try {
                option = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                wrongInput();
                continue;
            }
            switch (option) {
                case 1:
                    startNewGame();
                    break;
                case 2:
                    continueGame();
                    break;
                case 3:
                    loadGame();
                    break;
                case 4:
                    saveGame();
                    break;
                case 5:
                    this.game.running = false;
                default:
                    wrongInput();
            }
        }

        System.out.println("Vielen Dank für's Spielen! Ich hoffe, Sie hatten Spaß. " +
                "Bis zum nächsten Mal!");
    }

    private static void wrongInput() {
        System.out.println("Bitte wählen Sie eine der vorgegebenen Optionen " +
                "(Zahl ohne Klammer) und bestätigen Sie mit ENTER.");
    }

    private void printMenu() {
        String menuOutput = "(1) Neues Spiel starten\n";
        if (hasRunningGame()) {
            menuOutput += "(2) Spiel fortsetzen\n";
            menuOutput += hasSavedGame() ? "(3) Spiel laden\n" : "";
            menuOutput += "(4) Spiel speichern\n";
            menuOutput += "(5) Beenden";
        } else {
            menuOutput += "(2) Beenden";
        }
        System.out.println(menuOutput);
    }


    /**
     * Restores a game from the file "battleship.save"
     */
    private void loadGame() {
        if (!hasSavedGame()) {
            System.out.println("Kein gespeicherter Spielstand vorhanden.");
            return;
        }

        try {
            String saveGame = Files.readString(saveFilePath, StandardCharsets.UTF_8);
            String[] boards = saveGame.split("\n");
            Board playerBoard = new Board(boards[0]);
            Board villainBoard = new Board(boards[1]);
            this.game = new BattleshipGame(playerBoard, villainBoard);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Loading failed");
        }
    }

    /**
     * Saves a game into the file "battleship.save"
     */
    private void saveGame() {
        File file = saveFilePath.toFile();

        if (file.exists()) file.delete();
        try {
            file.createNewFile();

            String playerBoard = game.playerBoard.exportAsString();
            String villainBoard = game.villainBoard.exportAsString();
            Files.writeString(file.toPath(), playerBoard + villainBoard, StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Save failed");
        }
    }

    /**
     * Checks if file "battleship.save" exists
     * @return Whether the save file exists
     */
    private boolean hasSavedGame() {
        return saveFilePath.toFile().exists();
    }

    /**
     * Checks if game is running (or rather not null or finished)
     * @return Whether the game is running or not
     */
    private boolean hasRunningGame() {
        return !(game == null || game.isFinished());
    }

    /**
     * Continues the game
     */
    private void continueGame() {
        this.game.run();
    }

    /**
     * Starts a new game
     */
    private void startNewGame() {
        this.game = new BattleshipGame();
        continueGame();
    }

}

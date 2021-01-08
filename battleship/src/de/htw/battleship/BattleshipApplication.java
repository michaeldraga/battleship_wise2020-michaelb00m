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
        int option;
        while (true) {
            int nOfOptions = printMenu();
            System.out.print("\nOption: ");
            String input = new Scanner(System.in).nextLine();
            System.out.println();
            try {
                option = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                wrongInput();
                continue;
            }
            if (option == 1) {
                startNewGame();
            } else if (option == 2) {
                if (hasRunningGame())
                    continueGame();
                else if (hasSavedGame())
                    loadGame();
                else
                    break;
            } else if (option == 3) {
                if (hasRunningGame() && hasSavedGame())
                    loadGame();
                else if (hasRunningGame())
                    saveGame();
                else
                    break;
            } else if (option == 4) {
                if (hasRunningGame() && nOfOptions > 3)
                    saveGame();
                else
                    break;
            } else if (option == 5) {
                break;
            } else {
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

    private int printMenu() {
        int n = 0;
        boolean runningGame = hasRunningGame();
        String menuOutput = String.format("(%d) Neues Spiel starten\n", ++n);
        menuOutput += runningGame ? String.format("(%d) Spiel fortsetzen\n", ++n) : "";
        menuOutput += hasSavedGame() ? String.format("(%d) Spiel laden\n", ++n) : "";
        menuOutput += runningGame ? String.format("(%d) Spiel speichern\n", ++n) : "";
        menuOutput += String.format("(%d) Beenden", ++n);
        System.out.println(menuOutput);
        return n;
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

            System.out.println("Erfolgreich geladen.\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Laden fehlgeschlagen.\n");
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

            System.out.println("Erfolgreich gespeichert.\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Speichern fehlgeschlagen.\n");
        }
    }

    /**
     * Checks if file "battleship.save" exists
     *
     * @return Whether the save file exists
     */
    private boolean hasSavedGame() {
        return saveFilePath.toFile().exists();
    }

    /**
     * Checks if game is running (or rather not null or finished)
     *
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

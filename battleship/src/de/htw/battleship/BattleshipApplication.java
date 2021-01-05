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

    private void mainMenu() {

        // TODO print main menu to the console. let user select an option. (s. Aufgabe 3)


        startNewGame();





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
     */
    private boolean hasSavedGame() {
        return saveFilePath.toFile().exists();
    }

    private boolean hasRunningGame() {
        return !(game == null || game.isFinished());
    }

    private void continueGame() {
        this.game.run();
    }

    private void startNewGame() {
        this.game = new BattleshipGame();
        continueGame();
    }

}

package de.htw.battleship;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Requires Java 11 or higher.
 * Contains the starting point for the application.
 * An instance of this class can start new games, present the user different
 * menus, call methods based on their choices and can save and load games
 * and the High Score List.
 */
public class BattleshipApplication {

    private BattleshipGame game;
    private final Path saveFilePath = Path.of("battleship.save");
    private final Path highScoresFilePath = Path.of("highScores.save");
    private HighScores highScores = new HighScores();

    /**
     * Main method. Starts the BattleShipApplication and calls the mainMenu
     * function. Saves the high scores when the program finishes
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        BattleshipApplication battleshipApplication = new BattleshipApplication();
        if (battleshipApplication.hasSavedHighScores())
            battleshipApplication.loadHighScores();
        battleshipApplication.printHighScores();
        System.out.println("Herzlich Willkommen bei Battleships!\nMichael Draga wünscht ihnen " +
                "viel Vergnügen.\n");
        battleshipApplication.mainMenu();
        if (battleshipApplication.hasActiveHighScores())
            battleshipApplication.saveHighScores();
    }

    /**
     * Prints a menu to the user, lets them choose an option and executes the chosen option
     */
    private void mainMenu() {
        int option;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            int nOfOptions = printMenu();
            System.out.print("\nOption: ");
            String input = scanner.nextLine();
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
                if (hasRunningGame() && nOfOptions > 5)
                    saveGame();
                else
                    this.game.setAILevel(aiLevelMenu());
            } else if (option == 5) {
                if (hasRunningGame() && nOfOptions > 5)
                    this.game.setAILevel(aiLevelMenu());
                else
                    break;
            } else if (option == 6) {
                break;
            } else {
                wrongInput();
            }
        }
        System.out.println("Vielen Dank für's Spielen! Ich hoffe, Sie hatten Spaß. " +
                "Bis zum nächsten Mal!");
        scanner.close();
    }

    /**
     * Prints a menu for the AI difficulty level to the user, lets them choose
     * an option and returns the chosen difficulty
     *
     * @return The chosen AI difficulty level
     */
    private int aiLevelMenu() {
        int aiLevel = -1;
        while (true) {
            System.out.println("Welche der folgenden KI Schwierigkeitsstufen möchten Sie auswählen?");
            System.out.print("(0) Testschwierigkeit. Kann nicht gewinnen.%n" +
                    "(1) Leichteste Schwierigkeit. Platziert Schüsse zufällig.%n" +
                    "(2) Mittlere Schwierigkeit. Platziert Schüsse zufällig, schießt jedoch nicht auf Felder, die bereits beschossen wurden.%n" +
                    "(3) Fortgeschrittene Schwierigkeitstufe. Platziert Schüsse zufällig, bis ein  Schiff getroffen wurde. Ab diesem Zeitpunkt spielt die KI wie ein Mensch.%n" +
                    "(4) \"Sudden Death\". Die schwerste Schwierigkeitsstufe. Sobald die KI an den Zug kommt, hat sie gewonnen.%n" +

                    "Option: ");
            Scanner scanner = new Scanner(System.in);
            try {
                aiLevel = scanner.nextInt();
            } catch (InputMismatchException ignored) {
            }
            if (aiLevel >= 0 && aiLevel <= 4) {
                break;
            }
            System.out.println("Bitte geben Sie eine Zahl zwischen 0 und 4 ein.");
        }
        return aiLevel;
    }

    /**
     * Prints an error message to the user telling them to provide valid input
     */
    private static void wrongInput() {
        System.out.println("Bitte wählen Sie eine der vorgegebenen Optionen " +
                "(Zahl ohne Klammer) und bestätigen Sie mit ENTER.");
    }

    /**
     * Prints the main menu. The options are dynamically numbered based on the
     * number of options that will actually be printed
     *
     * @return The number of options that will be printed
     */
    private int printMenu() {
        int n = 0;
        boolean runningGame = hasRunningGame();
        String menuOutput = String.format("(%d) Neues Spiel starten%n", ++n);
        menuOutput += runningGame ? String.format("(%d) Spiel fortsetzen%n", ++n) : "";
        menuOutput += hasSavedGame() ? String.format("(%d) Spiel laden%n", ++n) : "";
        menuOutput += runningGame ? String.format("(%d) Spiel speichern%n", ++n) : "";
        menuOutput += runningGame ? String.format("(%d) AI Level ändern%n", ++n) : "";
        menuOutput += String.format("(%d) Beenden", ++n);
        System.out.println(menuOutput);
        return n;
    }

    /**
     * Public wrapper for the highScores.print method
     */
    public void printHighScores() {
        this.highScores.print();
    }

    /**
     * Restores the high score list from the file "highScores.save"
     */
    private void loadHighScores() {
        if (!hasSavedHighScores()) {
            System.out.println("Keine gespeicherten High Scores vorhanden.");
            return;
        }

        try {
            String savedHighScores = Files.readString(highScoresFilePath, StandardCharsets.UTF_8);
            String[] sHighScores = savedHighScores.split("\n");
            this.highScores = new HighScores();
            for (String sHighScore :
                    sHighScores) {
                String[] attributes = sHighScore.split(";");
                highScores.add(new Score(attributes[0], Integer.parseInt(attributes[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Laden fehlgeschlagen.\n");
        }
    }

    /**
     * Saves the high score list to the file "highScores.save"
     */
    private void saveHighScores() {
        File file = highScoresFilePath.toFile();

        if (file.exists()) file.delete();
        try {
            file.createNewFile();

            Files.writeString(file.toPath(), this.highScores.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Speichern fehlgeschlagen.\n");
        }
    }

    /**
     * Restores a game from the file "battleship.save"
     */
    private void loadGame() {
        if (!hasSavedHighScores()) {
            System.out.println("Kein gespeicherter Spielstand vorhanden.");
            return;
        }

        try {
            String saveGame = Files.readString(saveFilePath, StandardCharsets.UTF_8);
            String[] boards = saveGame.split("\n");
            Board playerBoard = new Board(boards[0], Board.stringToShips(boards[1]));
            Board villainBoard = new Board(boards[2], Board.stringToShips(boards[3]));
            int AILevel = Integer.parseInt(boards[4]);
            String playerName = boards[5];
            int shots = Integer.parseInt(boards[6]);
            this.game = new BattleshipGame(playerBoard, villainBoard, AILevel, playerName, shots);
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

            String output = this.game.playerBoard.exportAsString() +
                    this.game.villainBoard.exportAsString() +
                    this.game.getAILevel() + "\n" +
                    this.game.getPlayerName() + "\n" +
                    this.game.getShots() + "\n";
            Files.writeString(file.toPath(), output, StandardCharsets.UTF_8);

            System.out.println("Erfolgreich gespeichert.\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Speichern fehlgeschlagen.\n");
        }
    }

    /**
     * Checks if the highScore attribute has been initialized (is not null)
     *
     * @return Whether the highScore attribute has been initialized or not
     */
    private boolean hasActiveHighScores() {
        return this.highScores != null;
    }

    /**
     * Checks if file "highScores.save" exists
     *
     * @return Whether the high score save file exists
     */
    private boolean hasSavedHighScores() {
        return highScoresFilePath.toFile().exists();
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
        if (this.game.isFinished() && this.game.playerWon) {
            if (this.highScores == null) {
                if (this.hasSavedHighScores()) {
                    this.loadHighScores();
                } else {
                    this.highScores = new HighScores();
                }
            }
            if (!this.highScores.add(new Score(this.game.playerName, this.game.shots)))
                System.out.println("Ihr Score war zu niedrig, um der High Score Liste hinzugefügt werden " +
                        "zu können. Hoffentlich haben Sie nächstes Mal mehr Glück!");
            this.highScores.print();
        }
    }

    /**
     * Starts a new game
     */
    private void startNewGame() {
        int AILevel = aiLevelMenu();
        System.out.print("\nBitte geben Sie einen Namen für das Scoreboard ein: ");
        Scanner scanner = new Scanner(System.in);
        String playerName = scanner.nextLine().replace("\n", "");
        this.game = new BattleshipGame(AILevel, playerName);
        continueGame();
    }

}

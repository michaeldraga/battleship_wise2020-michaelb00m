package de.htw.battleship;

import java.util.Scanner;

/**
 * An instance of this class holds the state of a running match and the game logic.
 * @author Michael Draga
 * @version 1.0
 */
public class BattleshipGame {

    final Board playerBoard;
    final Board villainBoard;
    final AI villainAI;
    final String playerName;
    int shots = 0;
    boolean playerWon = false;

    /**
     * Set to TRUE to keep the game loop running. Set to FALSE to exit.
     */
    boolean running;

    /**
     * When playing, enemy ships should be hidden from the player.
     * Change below to FALSE for testing purposes during development of this program.
     */
    private final boolean hideVillainShips = true;

    /**
     * Creates a new game with new boards.
     * @param AILevel The chosen AI difficulty level
     * @param playerName The chosen player name
     */
    public BattleshipGame(int AILevel, String playerName) {
        this.playerBoard = new Board();
        this.villainBoard = new Board();
        this.villainAI = new AI(AILevel, this.playerBoard);
        this.playerName = playerName;
    }

    /**
     * Creates a game based on saved boards from a previous game.
     * @param playerBoard The saved player board
     * @param villainBoard The saved villain board
     * @param AILevel The saved AI difficulty level
     * @param playerName The saved name of the player
     * @param shots The saved amount of shots fired by the player in the saved game
     */
    public BattleshipGame(Board playerBoard, Board villainBoard, int AILevel, String playerName, int shots) {
        this.playerBoard = playerBoard;
        this.villainBoard = villainBoard;
        this.villainAI = new AI(AILevel, this.playerBoard);
        this.playerName = playerName;
        this.shots = shots;
    }


    /**
     * Main game loop. Keep running to play.
     * Interrupt the loop to get back to main menu.
     */
    public void run() {
        this.running = true;
        System.out.println("Spiel gestartet. Drücke ENTER während der Zieleingabe, im zum Hauptmenü zurückzukehren.\n");

        while (this.running) {
            playersTurn();
            if (this.running) villainsTurn();
        }
    }

    /**
     * Checks whether the input has the correct format
     * (first char is letter from a-z or A-Z, rest is a number less than 10,
     * string length less than 4)
     * @param input The input string that needs to be checked
     * @return Whether the given input has a valid format or not
     */
    private static boolean validateInput(String input) {
        try {
            return !(input.length() > 3 ||
                input.toUpperCase().charAt(0) - 65 < 0 ||
                input.toUpperCase().charAt(0) - 65 > 9 ||
                Integer.parseInt(input.substring(1)) < 0 ||
                Integer.parseInt(input.substring(1)) > 10);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Prints the result of the shot on the console, given the result code
     * @param result The result code returned from the Board.shoot method
     */
    private static void printResult(int result) {
        switch (result) {
            case 0:
                System.out.println("Daneben! Schade...");
                break;
            case 1: 
                System.out.println("Treffer!");
                break;
            case 2: 
                System.out.println("Piratenschiff versenkt!");
                break;
        }
    }

    /**
     * Prompts the player to input their shot and executes it.
     * If the player just hits enter they get back to the main menu.
     */
    private void playersTurn() {

        System.out.println("Spieler ist am Zug.");
        villainBoard.print(hideVillainShips);
        System.out.println();

        Vector2d playerShot;

        System.out.print("Feld: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (input.equals("")) {
            System.out.println("Spiel pausiert.");
            running = false;
            return;
        } else if (validateInput(input)) {
            playerShot = convertCoordinatesToInt(input);
        } else {
            System.out.println("Bitte geben Sie zuerst einen Buchstaben von A bis J (Großschreibung irrelevant)" +
                    " und direkt danach eine Zahl von 1 bis 10 ein. Beispiel: a1 (gleichbedeutend zu A1)");
            playersTurn();
            return;
        }
        int result = villainBoard.shoot(playerShot);
        shots++;

        System.out.println();

        villainBoard.print(hideVillainShips);

        printResult(result);

        villainBoard.deactivateLastMove();

        if (this.isFinished()) {
            System.out.println("\nSie haben gewonnen! Herzlichen Glückwunsch!\n");
            this.running = false;
            this.playerWon = true;
            return;
        }

        pause();
        if (result > 0)
            playersTurn();
    }

    /**
     * Lets the villain (computer) choose and play their shot.
     */
    private void villainsTurn() {

        System.out.println("Gegner ist am Zug.");
        playerBoard.print(false);
        Vector2d villainShot = getVillainShot();
        System.out.println();

        int result = playerBoard.shoot(villainShot);

        playerBoard.print(false);

        System.out.println();

        printResult(result);

        playerBoard.deactivateLastMove();

        if (result == 2)
            villainAI.loseMemory();

        if (this.isFinished()) {
            System.out.println("\nDer Gegner hat gewonnen. Hoffentlich hast du nächstes Mal mehr Glück!\n");
            this.running = false;
            return;
        }

        pause();

        if (result > 0) {
            villainsTurn();
        }
    }

    /**
     * Asks the user to press ENTER to continue.
     * Can be called anywhere in the game to avoid too much output at once.
     */
    private void pause() {
        System.out.println();
        System.out.println("Drücke ENTER um fortzufahren...");
        System.out.println();
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    /**
     * Gets an array with the two coordinates (x,y) the villain shoots at.
     * @return An array with the two coordinates of the villain's shot.
     */
    private Vector2d getVillainShot() {
        Vector2d shot = villainAI.nextMove();
        System.out.println("Gegner zielt auf " + convertCoordinatesToString(shot));
        return shot;
    }


    /**
     * Checks if game is finished
     * @return Whether game ist finished or not.
     */
    public boolean isFinished() {
        return playerBoard.isWholeFleetSunk() || villainBoard.isWholeFleetSunk();
    }


    /**
     * Converts alphanumeric board coordinates to array indexes, e.g. A1 to [0,0]
     * @param input The input string that needs to be converted
     * @return An array containing the coordinates from the input string.
     */
    public static Vector2d convertCoordinatesToInt(String input) {
        int x = input.toUpperCase().charAt(0) - 65;
        int y = Integer.parseInt(input.substring(1)) - 1;
        return new Vector2d(x, y);
    }

    /**
     * Converts a Vector2d to ahlphanumeric board coordinates, e.g. [0,0] to A1
     * @param input input coordinates that need to be converted
     * @return String coordinates made from the numeric board coordinates.
     */
    public static String convertCoordinatesToString(Vector2d input) {
        char x = (char) (input.x + 65);
        String y = Integer.toString(input.y + 1);
        return x + y;
    }

    /**
     * Wrapper for the setter of the difficulty level of the AI
     * @param AILevel The desired difficulty level for the AI
     */
    public void setAILevel(int AILevel) {
        this.villainAI.setLevel(AILevel);
    }

    /**
     * Wrapper for the getter of the difficulty level of the AI
     * @return The difficulty level of the AI
     */
    public int getAILevel() {
        return this.villainAI.getLevel();
    }

    /**
     * Getter of the attribute playerName
     * @return The name of the player currently playing
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Getter of the attribute shots
     * @return The shots that have been fired by the player in the current match
     */
    public int getShots() {
        return shots;
    }
}

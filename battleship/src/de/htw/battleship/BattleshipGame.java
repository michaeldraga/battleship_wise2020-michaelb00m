package de.htw.battleship;

import java.util.Random;
import java.util.Scanner;

/**
 * An instance of this class holds the state of a running match and the game logic.
 */
public class BattleshipGame {

    final Board playerBoard;
    final Board villainBoard;

    /**
     * Set to TRUE to keep the game loop running. Set to FALSE to exit.
     */
    boolean running;

    /**
     * When playing, enemy ships should be hidden from the player.
     * Change below to FALSE for testing purposes during development of this program.
     */
    private final boolean hideVillainShips = false;

    /**
     * Creates a new game with new boards.
     */
    public BattleshipGame() {
        this.playerBoard = new Board();
        this.villainBoard = new Board();
    }

    /**
     * Creates a game based on saved boards from a previous game.
     */
    public BattleshipGame(Board playerBoard, Board villainBoard) {
        this.playerBoard = playerBoard;
        this.villainBoard = villainBoard;
    }


    /**
     * Main game loop. Keep running to play.
     * Interrupt the loop to get back to main menu.
     */
    public void run() {
        running = true;
        System.out.println("Spiel gestartet. Drücke ENTER während der Zieleingabe, im zum Hauptmenü zurückzukehren.");

        while (running) {
            playersTurn();
            if (running) villainsTurn();
        }
    }

    private static boolean validateInput(String input) {
        try {
            return !(input.length() > 3 ||
                input.toUpperCase().charAt(0) - 65 < 0 ||
                input.toUpperCase().charAt(0) - 65 > 10 ||
                Integer.parseInt(input.substring(1)) < 0 ||
                Integer.parseInt(input.substring(1)) > 10);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Prompts the player to input their shot and executes it.
     * If the player just hits enter they get back to the main menu.
     */
    private void playersTurn() {

        System.out.println("Spieler ist am Zug.");
        villainBoard.print(hideVillainShips);

        int[] playerShot = null;

        // TODO (s. Aufgabe 5)
        String input = new Scanner(System.in).nextLine();
        if (validateInput(input)) {
            playerShot = convertCoordinatesToInt(input);
        }
        // player wants to exit game
        if (playerShot == null) {
            System.out.println("Spiel pausiert.");
            running = false;
            return;
        }
        int result = villainBoard.shoot(playerShot);

        switch (result) {
            case 0 -> System.out.println("Daneben! Schade...");
            case 1 -> System.out.println("Treffer!");
            case 2 -> System.out.println("Piratenschiff versenkt!");
        }

        if (result > 0)
            playersTurn();
    }

    /**
     * Lets the villain (computer) choose and play their shot.
     */
    private void villainsTurn() {

        System.out.println("Gegner ist am Zug.");
        playerBoard.print(false);
        int[] villainShot = getVillainShot();

        // TODO (s. Aufgabe 6)

        pause();
    }

    /**
     * Asks the user to press ENTER to continue.
     * Can be called anywhere in the game to avoid too much output at once.
     */
    private void pause() {
        System.out.println();
        System.out.println("Drücke ENTER um fortzufahren...");
        System.out.println();
        new Scanner(System.in).nextLine();
    }

    /**
     * Gets an array with the two coordinates (x,y) the villain shoots at.
     * @return An array with the two coordinates of the villain's shot.
     */
    private int[] getVillainShot() {
        int x;
        int y;

        // Strategy to aim a shot: Pick a random field that is empty
        do {
            x = new Random().nextInt(Board.BOARD_SIZE);
            y = new Random().nextInt(Board.BOARD_SIZE);
        } while (playerBoard.getField(x, y) != Board.EMPTY);

        int[] shot = new int[]{x, y};
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
     * @return An array containing the coordinates from the input string.
     */
    public static int[] convertCoordinatesToInt(String input) {
        int x = input.toUpperCase().charAt(0) - 65;
        int y = Integer.parseInt(input.substring(1)) - 1;
        return new int[]{x, y};
    }

    /**
     * Converts array indexes to ahlphanumeric board coordinates, e.g. [0,0] to A1
     * @return String coordinates made from the numeric board coordinates.
     */
    public static String convertCoordinatesToString(int[] input) {
        char x = (char) (input[0] + 65);
        String y = Integer.toString(input[1]);
        return x + y;
    }
}

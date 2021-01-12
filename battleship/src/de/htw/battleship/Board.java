package de.htw.battleship;

import java.util.ArrayList;
import java.util.Random;

/**
 * Holds the state of one players board
 * as well as the methods to generate a board and process shots.
 */
public class Board {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final char EMPTY = '.';
    public static final char SHIP = 'O';
    public static final char HIT = 'X';
    public static final char MISSED_SHOT = '-';

    public static final int BOARD_SIZE = 10;

    public static final int[] shipLengths = new int[]{5, 4, 3, 3, 2, 2};

    private final char[][] fields = new char[BOARD_SIZE][BOARD_SIZE];
    private final ArrayList<Ship> ships = new ArrayList<>();
    private Vector2d lastMove = new Vector2d(-1, -1);

    public int shoot(int[] coordinates) {
        int x = coordinates[0];
        int y = coordinates[1];
        if (fields[x][y] == HIT)
            return 0;
        this.lastMove = new Vector2d(x, y);
        boolean hit = fields[x][y] == SHIP;
        if (!hit) {
            fields[x][y] = MISSED_SHOT;
            return 0;
        }
        fields[x][y] = HIT;
        for (Ship ship :
                ships) {
            if (isShipSunk(ship)) {
                ships.remove(ship);
                return 2;
            }
        }
        return 1;
    }

    private boolean isShipSunk(Ship ship) {
        int right = ship.isHorizontal() ? 1 : 0;
        int down = ship.isVertical() ? 1 : 0;
        for (int i = 0; i < ship.getShipLength(); i++) {
            if (fields[ship.x + i * right][ship.y + i * down] == SHIP) {
                return false;
            }
        }
        return true;
    }

    private boolean checkSurroundings(int x, int y) {
        int left = x == 0 ? 0 : 1;
        int right = x >= BOARD_SIZE - 1 ? 0 : 1;
        int up = y == 0 ? 0 : 1;
        int down = y >= BOARD_SIZE - 1 ? 0 : 1;
        return fields[x - left][y - up] == SHIP ||
                fields[x - left][y] == SHIP ||
                fields[x - left][y + down] == SHIP ||
                fields[x][y - up] == SHIP ||
                fields[x][y] == SHIP ||
                fields[x][y + down] == SHIP ||
                fields[x + right][y - up] == SHIP ||
                fields[x + right][y] == SHIP ||
                fields[x + right][y + down] == SHIP;
    }

    private Ship generateShipPosition(int shipLength) {
        Random r = new Random();
        boolean horizontal;
        while (true) {
            horizontal = r.nextBoolean();
            Vector2d position = new Vector2d(r.nextInt(BOARD_SIZE - (horizontal ? shipLength : 0)),
                    r.nextInt(BOARD_SIZE - (!horizontal ? shipLength : 0)));
            boolean collides = false;
            for (int i = 0; i < shipLength; i++) {
                if (checkSurroundings(position.x + (horizontal ? i : 0),
                        position.y + (!horizontal ? i : 0))) {
                    collides = true;
                    break;
                }
            }
            if (collides)
                continue;
            Ship newShip = new Ship(position, shipLength, horizontal);
            this.ships.add(newShip);
            return newShip;
        }
    }

    public void deactivateLastMove() {
        this.lastMove = new Vector2d(-1, -1);
    }

    /**
     * Create a new Board and generate ships
     */
    public Board() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                fields[i][j] = EMPTY;
            }
        }

        for (int shipLength : shipLengths) {
            Ship ship = generateShipPosition(shipLength);
            boolean horizontal = ship.isHorizontal();
            boolean vertical = ship.isVertical();
            for (int j = 0; j < shipLength; j++) {
                fields[ship.x + (horizontal ? j : 0)][ship.y + (vertical ? j : 0)] = SHIP;
            }
        }


    }

    /**
     * Create a Board from an exported string.
     */
    public Board(String savedBoard) {
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                int index = y * BOARD_SIZE + x;
                fields[x][y] = savedBoard.charAt(index);
            }
        }
    }

    /**
     * Prints the board to System.out
     *
     * @param hideShips if TRUE, replaces ships by empty fields in output
     */
    public void print(boolean hideShips) {
        /* print column headers A - J */
        System.out.print("# ");
        for (int x = 0; x < fields[0].length; x++) {
            char column = (char) (x + 65);
            System.out.print(" " + column);
        }
        System.out.println();

        for (int y = 0; y < fields.length; y++) {
            /* print row number */
            int rowNumber = y + 1;
            System.out.print(rowNumber + " ");
            if (rowNumber < 10) System.out.print(" ");

            /* print row */
            for (int x = 0; x < fields[y].length; x++) {
                char output = fields[x][y];
                if (output == SHIP && hideShips)
                    output = EMPTY;
                System.out.print(((lastMove.x == x && lastMove.y == y) ? ANSI_GREEN :
                        output == SHIP ? ANSI_BLUE :
                        output == HIT ? ANSI_RED :
                        output == MISSED_SHOT ? ANSI_YELLOW : "")
                        + output
                        + (output != EMPTY ? ANSI_RESET : "")
                        + " ");
            }
            System.out.println();
        }
    }

    /**
     * Exports the board as one string.
     *
     * @return A string containing the board fields
     */
    public String exportAsString() {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                builder.append(fields[x][y]);
            }
        }
        builder.append("\n");
        return builder.toString();
    }

    /**
     * Checks if the whole fleet is sunk.
     *
     * @return FALSE if at least one ship is remaining. TRUE otherwise.
     */
    public boolean isWholeFleetSunk() {
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                if (fields[x][y] == SHIP)
                    return false;
            }
        }
        return true;
    }

    /**
     * Gets the value of the field at coordinates x, y
     *
     * @param x x coordinate on the board.
     * @param y y coordinate on the board.
     * @return The value of the specified field.
     */
    public char getField(int x, int y) {
        return fields[x][y];
    }
}

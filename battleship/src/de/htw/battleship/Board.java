package de.htw.battleship;

import java.util.Random;

/**
 * Holds the state of one players board
 * as well as the methods to generate a board and process shots.
 */
public class Board {

    public static final char EMPTY = '.';
    public static final char SHIP = 'O';
    public static final char HIT = 'X';
    public static final char MISSED_SHOT = '-';

    public static final int BOARD_SIZE = 10;

    public static final int[] ships = new int[]{5, 4, 3, 3, 2, 2};

    private final char[][] fields = new char[BOARD_SIZE][BOARD_SIZE];

    private static boolean horizontal() {
        Random r = new Random();
        return r.nextInt(2) == 1;
    }

    private boolean checkSurroundings(int x, int y) {
        int left = x == 0 ? 0 : 1;
        int right = x >= BOARD_SIZE - 1 ? 0 : 1;
        int up = y == 0 ? 0 : 1;
        int down = y >= BOARD_SIZE - 1 ? 0 : 1;
        return fields[y - up][x - left] == SHIP ||
                fields[y - up][x] == SHIP ||
                fields[y - up][x + right] == SHIP ||
                fields[y][x - left] == SHIP ||
                fields[y][x] == SHIP ||
                fields[y][x + right] == SHIP ||
                fields[y + down][x - left] == SHIP ||
                fields[y + down][x] == SHIP ||
                fields[y + down][x + right] == SHIP;
    }

    private int[] generatePosition(int shipLength) {
        Random r = new Random();
        boolean horizontal;
        while (true) {
            horizontal = r.nextBoolean();
            int x = r.nextInt(BOARD_SIZE - (horizontal ? shipLength : 0));
            if (!horizontal) {
                System.out.println(x + shipLength);
            }
            int y = r.nextInt(BOARD_SIZE - (!horizontal ? shipLength : 0));
            boolean collides = false;
            for (int i = 0; i < shipLength; i++) {
                if (checkSurroundings(x + (horizontal ? i : 0),
                        y + (!horizontal ? i : 0))) {
                    collides = true;
                    break;
                }
            }
            if (collides)
                continue;
            return new int[]{x, y, horizontal ? 1 : 0};
        }

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

        // TODO generate ships (s. Aufgabe 4)+
        for (int i = 0; i < ships.length; i++) {
            int shipLength = ships[i];
//            System.out.println(shipLength);
            int[] position = generatePosition(shipLength);
//            System.out.println(position[0] + " " + position[1] + " " + position[2]);
            boolean horizontal = position[2] == 1;
            boolean vertical = position[2] == 0;
            for (int j = 0; j < shipLength; j++) {
                fields[position[1] + (vertical ? j : 0)][position[0] + (horizontal ? j : 0)] = SHIP;
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
                System.out.print(output + " ");
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
     * @return The value of the speicified field.
     */
    public char getField(int x, int y) {
        return fields[x][y];
    }
}

package de.htw.battleship;

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

    public static final int[] ships = new int[]{5, 4, 3, 3, 2, 2};

    private final char[][] fields = new char[BOARD_SIZE][BOARD_SIZE];
    private final int[][] shipPositions = new int[6][4];

    public int shoot(int[] coordinates) {
        int x = coordinates[0];
        int y = coordinates[1];
        if (fields[x][y] == HIT)
            return 0;
        boolean hit = fields[x][y] == SHIP;
        if (!hit) {
            fields[x][y] = MISSED_SHOT;
            return 0;
        }
        fields[x][y] = HIT;
        for (int i = 0; i < shipPositions.length; i++) {
            if (shipPositions[i] == null)
                continue;
            if (isShipSunk(shipPositions[i][0], shipPositions[i][1], shipPositions[i][2], shipPositions[i][3])) {
                shipPositions[i] = null;
                return 2;
            }
        }
        return hit ? 1 : 0;
    }

    private void addPosition(int x, int y, int shipLength, int horizontal) {
        for (int i = 0; i < shipPositions.length; i++) {
            if (shipPositions[i][2] == 0) {
                shipPositions[i][0] = x;
                shipPositions[i][1] = y;
                shipPositions[i][2] = shipLength;
                shipPositions[i][3] = horizontal;
                return;
            }
        }
    }

    private boolean isShipSunk(int x, int y, int shipLength, int horizontal) {
        int right = horizontal;
        int down = (horizontal - 1) * -1;
        for (int i = 0; i < shipLength; i++) {
            if (fields[x + i * right][y + i * down] == SHIP) {
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

    private int[] generatePosition(int shipLength) {
        Random r = new Random();
        boolean horizontal;
        while (true) {
            horizontal = r.nextBoolean();
            int x = r.nextInt(BOARD_SIZE - (horizontal ? shipLength : 0));
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
            this.addPosition(x, y, shipLength, horizontal ? 1 : 0);
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
            int[] position = generatePosition(shipLength);
            boolean horizontal = position[2] == 1;
            boolean vertical = position[2] == 0;
            for (int j = 0; j < shipLength; j++) {
                fields[position[0] + (horizontal ? j : 0)][position[1] + (vertical ? j : 0)] = SHIP;
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
                System.out.print((output == SHIP ? ANSI_BLUE : output == HIT ? ANSI_RED : output == MISSED_SHOT ? ANSI_YELLOW : "") + output + (output != EMPTY ? ANSI_RESET : "") + " ");
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

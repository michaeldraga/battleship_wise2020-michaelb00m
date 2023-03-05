package de.htw.battleship;

import java.util.ArrayList;
import java.util.Random;

/**
 * Holds the state of one players board
 * as well as the methods to generate a board and process shots.
 * @author Michael Draga
 * @version 1.0
 */
public class Board {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";

    public static final char EMPTY = '.';
    public static final char SHIP = 'O';
    public static final char HIT = 'X';
    public static final char MISSED_SHOT = '-';

    public static final int BOARD_SIZE = 10;

    private static final int[] shipLengths = new int[]{5,4,3,3,2,2};

    private final char[][] fields = new char[BOARD_SIZE][BOARD_SIZE];
    private final ArrayList<Ship> ships;
    private Vector2d lastMove = new Vector2d(-1, -1);

    /**
     * Parses the ships from the saved string into Ships and returns an ArrayList
     * containing them
     * @param savedBoards The saved string containing the ships
     * @return An ArrayList containing all saved ships from the string
     */
    public static ArrayList<Ship> stringToShips(String savedBoards) {
        ArrayList<Ship> savedShips = new ArrayList<Ship>();
        String[] sBoardsArray = savedBoards.split(";");
        for (String sBoard :
                sBoardsArray) {
            String[] attributes = sBoard.split(",");
            Ship sShip = new Ship(Integer.parseInt(attributes[0]), Integer.parseInt(attributes[1]),
                                Integer.parseInt(attributes[2]), Boolean.parseBoolean(attributes[3]));
            savedShips.add(sShip);
        }
        return savedShips;
    }

    /**
     * "Shoots" the given coordinates and checks whether the shot resulted
     * in a HIT, a MISSED_SHOT or even in a sunk ship.
     * @param coordinates The Vector2d representing the coordinates being shot at
     * @return The result code of the shot (0: miss/already hit, 1: hit, 2: ship sunk)
     */
    public int shoot(Vector2d coordinates) {
        int x = coordinates.x;
        int y = coordinates.y;
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

    /**
     * Checks whether a ship is sunk and returns the result
     * @param ship The ship being checked
     * @return Whether the ship is sunk or not
     */
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

    /**
     * Checks if the field at the given coordinates or any of its surrounding
     * fields contain a SHIP
     * @param x The x coordinate of the field being checked
     * @param y The y coordinate of the field being checked
     * @return Whether the field or any of its surrounding fields contain a ship
     */
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

    /**
     * Generates a new ship at a random position, with a random orientation
     * of the given ship length, that does not intersect or touch any other
     * ship.
     * @param shipLength The desired length of the new ship
     * @return A Ship object representing the new ship
     */
    private Ship generateShip(int shipLength) {
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

    /**
     * "Deactivates" the last move by replacing it with a Vector2d containing
     * impossible values (values that can not be generated elsewhere in code)
     */
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
        this.ships = new ArrayList<Ship>();
        for (int shipLength : shipLengths) {
            Ship ship = generateShip(shipLength);
            boolean horizontal = ship.isHorizontal();
            boolean vertical = ship.isVertical();
            for (int j = 0; j < shipLength; j++) {
                fields[ship.x + (horizontal ? j : 0)][ship.y + (vertical ? j : 0)] = SHIP;
            }
        }
    }

    /**
     * Create a Board and add its ships from an exported string.
     * @param savedBoard The saved string representation of the board object
     * @param savedShips An ArrayList containing the saved ships
     */
    public Board(String savedBoard, ArrayList<Ship> savedShips) {
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                int index = y * BOARD_SIZE + x;
                fields[x][y] = savedBoard.charAt(index);
            }
        }
        this.ships = savedShips;
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
        builder.append(this.convertShipsToString());
        return builder.toString();
    }

    /**
     * Exports the ships as one string.
     * @return A string containing the ship attributes
     */
    private String convertShipsToString() {
        StringBuilder builder = new StringBuilder();
        for (Ship ship:
             ships) {
            builder.append(ship.toString()).append(";");
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

    /**
     * Gets the value of the field at coordinates x, y
     * @param position A Vector2d representing the position of the desired field
     * @return The value of the desired field
     */
    public char getField(Vector2d position) {
        return fields[position.x][position.y];
    }
}

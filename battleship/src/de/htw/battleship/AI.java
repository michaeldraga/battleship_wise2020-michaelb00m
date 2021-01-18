package de.htw.battleship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class AI {
    private final ArrayList<int[]> lastMoves = new ArrayList<>();
    private final ArrayList<int[]> testedDifferences = new ArrayList<>();
    private final int level;
    private final Board playerBoard;

    private Boolean horizontal;
    private int direction;

    /**
     * Constructor of the class
     * @param level The difficulty level of the AI
     * @param playerBoard The board of the player
     */
    public AI(int level, Board playerBoard) {
        this.level = level;
        this.playerBoard = playerBoard;
    }

    /**
     * Determines the algorithm used for the AI's next move based on the
     * difficulty level
     * @return The move returned from the executed algorithm
     */
    public int[] nextMove() {
        return switch (this.level) {
            case 0 -> level0Algorithm();
            case 1 -> level1Algorithm();
            case 2 -> level2Algorithm();
            case 3 -> level3Algorithm();
            case 4 -> level4Algorithm();
            default -> new int[2];
        };
    }

    /**
     * Algorithm for AI testing difficulty.
     * Designed to not hit any ships --> Cannot win.
     * @return The position to play in the next move
     */
    private int[] level0Algorithm() {
        int x;
        int y;

        // Strategy to aim a shot: Pick a random field that is empty
        do {
            x = new Random().nextInt(Board.BOARD_SIZE);
            y = new Random().nextInt(Board.BOARD_SIZE);
        } while (playerBoard.getField(x, y) != Board.MISSED_SHOT && playerBoard.getField(x, y) == Board.HIT);

        return new int[]{x, y};
    }

    /**
     * Algorithm for easiest AI difficulty.
     * Chooses the next played position completely random.
     * @return The position to play in the next move
     */
    private int[] level1Algorithm() {
        Random r = new Random();
        return new int[]{r.nextInt(Board.BOARD_SIZE), r.nextInt(Board.BOARD_SIZE)};
    }

    /**
     * Algorithm for medium AI difficulty.
     * Chooses the next played position random but does not choose any fields
     * that have already been hit (or missed).
     * @return The position to play in the next move
     */
    private int[] level2Algorithm() {
        Random r = new Random();
        int x, y;
        do {
            x = r.nextInt(Board.BOARD_SIZE);
            y = r.nextInt(Board.BOARD_SIZE);
        } while (playerBoard.getField(x, y) == Board.HIT || playerBoard.getField(x, y) == Board.MISSED_SHOT);
        return new int[]{x, y};
    }

    /**
     * Algorithm for advanced AI difficulty.
     * Chooses the next played position random until it hits a ship. From that
     * point on it plays pretty much like a human would: it shoots the fields
     * next to it; as soon as it hits another part of the ship, it advances
     * in that direction; if it reaches the end (of the ship or the board)
     * without having sunk the ship, it returns to the starting position and
     * continues in the other direction until the ship is sunk.
     * @return The position to play in the next move
     */
    private int[] level3Algorithm() {
        if (lastMoves.isEmpty()) {
            Random r = new Random();
            int x, y;
            do {
                x = r.nextInt(Board.BOARD_SIZE);
                y = r.nextInt(Board.BOARD_SIZE);
            } while (this.playerBoard.getField(x, y) == Board.HIT || this.playerBoard.getField(x, y) == Board.MISSED_SHOT);
            if (this.playerBoard.getField(x, y) == Board.SHIP)
                lastMoves.add(new int[]{x, y});
            return new int[]{x, y};
        }
        if (horizontal == null) {
            Random r = new Random();
            int[] position = lastMoves.get(0);
            int direction, dx, dy, difference;
            do {
                if (this.testedDifferences.size() % 2 == 1) {
                    int[] differences = this.testedDifferences.get(this.testedDifferences.size() - 1);
                    dx = differences[1];
                    dy = differences[0];
                } else {
                    direction = r.nextInt(4);
                    difference = direction - 2;
                    dx = difference % 2;
                    dy = (difference + 1) % 2;
                }
                if (collidesWithBorder(position[0] + dx) ||
                        collidesWithBorder(position[1] + dy) ||
                        this.playerBoard.getField(position[0] + dx, position[1] + dy) == Board.MISSED_SHOT)
                    this.testedDifferences.add(new int[]{dx, dy});
            } while (listContainsArray(this.testedDifferences, new int[]{dx, dy}));
            this.testedDifferences.add(new int[]{dx, dy});
            int[] move = new int[]{position[0] + dx,
                    position[1] + dy};
            if (hitsShip(move)) {
                this.lastMoves.add(move);
                this.horizontal = dx % 2 != 0;
                int sign = Integer.signum(this.horizontal ? dx : dy);
                this.direction = sign == 0 ? 1 : sign;
            }
            return move;
        }
        int[] move = advance(this.lastMoves.get(lastMoves.size() - 1));
        if (collidesWithBorder(move[0]) ||
                collidesWithBorder(move[1]) ||
                hitsAgain(move)) {
            this.resetAndTurnAround();
            move = advance(this.lastMoves.get(lastMoves.size() - 1));
            this.lastMoves.add(move);
            return move;
        }
        if (this.misses(move)) {
            this.resetAndTurnAround();
            return move;
        }
        this.lastMoves.add(move);
        return move;
    }

    /**
     * Algorithm for the hardest AI difficulty.
     * Given the nickname "sudden death", this algorithm makes the AI win as
     * soon as it gets to play. The algorithm is designed to only hit fields
     * that contain a ship.
     * @return The position to play in the next move
     */
    private int[] level4Algorithm() {
        Random r = new Random();
        int x, y;
        do {
            x = r.nextInt(Board.BOARD_SIZE);
            y = r.nextInt(Board.BOARD_SIZE);
        } while (playerBoard.getField(x,y) != Board.SHIP);
        return new int[] {x,y};
    }

    /**
     * Checks if an ArrayList\<int[]\> contains a specific int[]
     * @param arrayList The array list to be checked
     * @param ints The int[] to be checked
     * @return Whether the given array list contains the given int[]
     */
    private boolean listContainsArray(ArrayList<int[]> arrayList, int[] ints) {
        for (int[] listInts :
                arrayList) {
            if (listInts != null) {
                if (Arrays.equals(listInts, ints))
                    return true;
            }
        }
        return false;
    }

    /**
     * Calculates the next field in the current direction.
     * @param move The last "move" (position)
     * @return The next field in the current direction
     */
    private int[] advance(int[] move) {
        return new int[]{move[0] + (this.horizontal ? 1 : 0) * this.direction,
                move[1] + (!this.horizontal ? 1 : 0) * this.direction};
    }

    /**
     * Checks whether a value is outside of the borders of the board.
     * @param point The point being checked
     * @return Whether the point lies outside of the borders of the board
     */
    private static boolean collidesWithBorder(int point) {
        return point < 0 || point >= Board.BOARD_SIZE;
    }

    /**
     * Checks whether the field at the given position was already shot at
     * (whether it is a MISSED_SHOT or a HIT)
     * @param move The position of the field being checked
     * @return Whether the field at the given position was already shot at
     */
    private boolean hitsAgain(int[] move) {
        return this.playerBoard.getField(move[0], move[1]) == Board.MISSED_SHOT ||
                this.playerBoard.getField(move[0], move[1]) == Board.HIT;
    }

    /**
     * Checks whether the field at the given position is a SHIP
     * @param move The position of the field being checked
     * @return Whether the field at the given position is a SHIP
     */
    private boolean hitsShip(int[] move) {
        return this.playerBoard.getField(move[0], move[1]) == Board.SHIP;
    }

    /**
     * Checks whether the field at the given position is a MISS
     * @param move The position of the field being checked
     * @return Whether the field at the given position is a MISS
     */
    private boolean misses(int[] move) {
        return this.playerBoard.getField(move[0], move[1]) == Board.EMPTY;
    }

    /**
     * Reverses the current direction and goes back to the starting point
     * of the current ship to keep shooting in the other direction
     */
    private void resetAndTurnAround() {
        this.direction *= -1;
        int[] startingPoint = lastMoves.get(0);
        this.lastMoves.clear();
        this.lastMoves.add(startingPoint);
    }

    /**
     * Deletes the AI's "memory" by resetting all ship-specific attribute
     * values (orientation, direction, lastMoves and testedDifferences)
     */
    public void loseMemory() {
        this.horizontal = null;
        this.direction = 0;
        this.testedDifferences.clear();
        this.lastMoves.clear();
    }
}

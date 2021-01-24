package de.htw.battleship;

import java.util.ArrayList;
import java.util.Random;

/**
 * An instance of this class can generate shots for the villain (computer) to
 * play, using 5 algorithms with different difficulties.
 * @author Michael Draga
 * @version 1.0
 */
public class AI {
    private final ArrayList<Vector2d> lastMoves = new ArrayList<Vector2d>();
    private final ArrayList<Vector2d> testedDifferences = new ArrayList<Vector2d>();
    private int level;
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
    public Vector2d nextMove() {
        Vector2d nextMove;
        switch (this.level) {
            case 0:
                nextMove = level0Algorithm();
                break;
            case 1:
                nextMove = level1Algorithm();
                break;
            case 2:
                nextMove = level2Algorithm();
                break;
            case 3:
                nextMove = level3Algorithm();
                break;
            case 4:
                nextMove = level4Algorithm();
                break;
            default:
                nextMove = new Vector2d();
                break;
        };
        return nextMove;
    }

    /**
     * Algorithm for AI testing difficulty.
     * Designed to not hit any ships, cannot win.
     * @return The position to play in the next move
     */
    private Vector2d level0Algorithm() {
        int x;
        int y;

        // Strategy to aim a shot: Pick a random field that is empty
        do {
            x = new Random().nextInt(Board.BOARD_SIZE);
            y = new Random().nextInt(Board.BOARD_SIZE);
        } while (playerBoard.getField(x, y) != Board.EMPTY && playerBoard.getField(x, y) != Board.MISSED_SHOT);

        return new Vector2d(x, y);
    }

    /**
     * Algorithm for easiest AI difficulty.
     * Chooses the next played position completely random.
     * @return The position to play in the next move
     */
    private Vector2d level1Algorithm() {
        Random r = new Random();
        return new Vector2d(r.nextInt(Board.BOARD_SIZE), r.nextInt(Board.BOARD_SIZE));
    }

    /**
     * Algorithm for medium AI difficulty.
     * Chooses the next played position random but does not choose any fields
     * that have already been hit (or missed).
     * @return The position to play in the next move
     */
    private Vector2d level2Algorithm() {
        Random r = new Random();
        int x, y;
        do {
            x = r.nextInt(Board.BOARD_SIZE);
            y = r.nextInt(Board.BOARD_SIZE);
        } while (playerBoard.getField(x, y) == Board.HIT || playerBoard.getField(x, y) == Board.MISSED_SHOT);
        return new Vector2d(x, y);
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
    private Vector2d level3Algorithm() {
        if (lastMoves.isEmpty()) {
            Random r = new Random();
            int x, y;
            do {
                x = r.nextInt(Board.BOARD_SIZE);
                y = r.nextInt(Board.BOARD_SIZE);
            } while (this.playerBoard.getField(x, y) == Board.HIT || this.playerBoard.getField(x, y) == Board.MISSED_SHOT);
            if (this.playerBoard.getField(x, y) == Board.SHIP)
                lastMoves.add(new Vector2d(x, y));
            return new Vector2d(x, y);
        }
        if (horizontal == null) {
            Random r = new Random();
            Vector2d position = lastMoves.get(0);
            int direction, dx, dy, difference;
            do {
                if (this.testedDifferences.size() % 2 == 1) {
                    Vector2d differences = this.testedDifferences.get(this.testedDifferences.size() - 1);
                    dx = differences.x;
                    dy = differences.y;
                } else {
                    direction = r.nextInt(4);
                    difference = direction - 2;
                    dx = difference % 2;
                    dy = (difference + 1) % 2;
                }
                if (collidesWithBorder(position.x + dx) ||
                        collidesWithBorder(position.y + dy) ||
                        this.playerBoard.getField(position.x + dx, position.y + dy) == Board.MISSED_SHOT)
                    this.testedDifferences.add(new Vector2d(dx, dy));
            } while (listContainsArray(this.testedDifferences, new Vector2d(dx, dy)));
            this.testedDifferences.add(new Vector2d(dx, dy));
            Vector2d move = new Vector2d(position.x + dx,
                    position.y + dy);
            if (hitsShip(move)) {
                this.lastMoves.add(move);
                this.horizontal = dx % 2 != 0;
                int sign = Integer.signum(this.horizontal ? dx : dy);
                this.direction = sign == 0 ? 1 : sign;
            }
            return move;
        }
        Vector2d move = advance(this.lastMoves.get(lastMoves.size() - 1));
        if (collidesWithBorder(move.x) ||
                collidesWithBorder(move.y) ||
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
    private Vector2d level4Algorithm() {
        Random r = new Random();
        int x, y;
        do {
            x = r.nextInt(Board.BOARD_SIZE);
            y = r.nextInt(Board.BOARD_SIZE);
        } while (playerBoard.getField(x,y) != Board.SHIP);
        return new Vector2d(x,y);
    }

    /**
     * Checks if an ArrayList of type Vector2d contains a specific Vector2d
     * @param arrayList The array list to be checked
     * @param vector The int[] to be checked
     * @return Whether the given array list contains the given int[]
     */
    private boolean listContainsArray(ArrayList<Vector2d> arrayList, Vector2d vector) {
        for (Vector2d listVector :
                arrayList) {
            if (listVector != null) {
                if (Vector2d.equals(listVector, vector))
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
    private Vector2d advance(Vector2d move) {
        return new Vector2d(move.x + (this.horizontal ? 1 : 0) * this.direction,
                move.y + (!this.horizontal ? 1 : 0) * this.direction);
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
    private boolean hitsAgain(Vector2d move) {
        return this.playerBoard.getField(move) == Board.MISSED_SHOT ||
                this.playerBoard.getField(move) == Board.HIT;
    }

    /**
     * Checks whether the field at the given position is a SHIP
     * @param move The position of the field being checked
     * @return Whether the field at the given position is a SHIP
     */
    private boolean hitsShip(Vector2d move) {
        return this.playerBoard.getField(move) == Board.SHIP;
    }

    /**
     * Checks whether the field at the given position is a MISS
     * @param move The position of the field being checked
     * @return Whether the field at the given position is a MISS
     */
    private boolean misses(Vector2d move) {
        return this.playerBoard.getField(move.x, move.y) == Board.EMPTY;
    }

    /**
     * Reverses the current direction and goes back to the starting point
     * of the current ship to keep shooting in the other direction
     */
    private void resetAndTurnAround() {
        this.direction *= -1;
        Vector2d startingPoint = lastMoves.get(0);
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

    /**
     * Getter for the level attribute
     * @return The value of the level attribute (the AIs difficulty level)
     */
    public int getLevel() {
        return level;
    }

    /**
     * Setter for the level attribute
     * @param level The desired difficulty level for the AI
     */
    public void setLevel(int level) {
        this.level = level;
    }
}

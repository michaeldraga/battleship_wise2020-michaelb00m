package de.htw.battleship;

/**
 * An instance of this class holds all information to render and identify
 * a ship on a game board. Additionally, it can be exported as a string.
 * @author Michael Draga
 * @version 1.0
 */
public class Ship extends Vector2d {
    private int shipLength;
    private boolean horizontal;

    /**
     * Generate a new ship with given values for x and y
     * @param x The x coordinate of the starting point
     * @param y The y coordinate of the starting point
     */
    public Ship(int x, int y) {
        super(x, y);
    }

    /**
     * Generate new ship with given values for the starting position,
     * ship length and orientation
     * @param x The x coordinate of the starting point
     * @param y The y coordinate of the starting point
     * @param shipLength The length of the ship
     * @param horizontal The orientation of the ship
     */
    public Ship(int x, int y, int shipLength, boolean horizontal) {
        super(x, y);
        this.shipLength = shipLength;
        this.horizontal = horizontal;
    }

    /**
     * Generate new ship with given values for the starting position,
     * ship length and orientation
     * @param position A Vector2d representing the position of the starting point
     * @param shipLength The length of the ship
     * @param horizontal The orientation of the ship
     */
    public Ship(Vector2d position, int shipLength, boolean horizontal) {
        super();
        this.x = position.x;
        this.y = position.y;
        this.shipLength = shipLength;
        this.horizontal = horizontal;
    }

    /**
     * Converts a ship into it's string representation (a list of its attributes)
     * @return The string representation of a ship
     */
    public String toString() {
        return String.format("%d,%d,%d,%b", this.x, this.y, this.shipLength, this.horizontal);
    }

    /**
     * Checks if the ship is horizontal
     * @return Whether the ship is horizontal or not
     */
    public boolean isHorizontal() {
        return this.horizontal;
    }

    /**
     * Checks if the ship is vertical
     * @return Whether the ship is vertical or not
     */
    public boolean isVertical() {
        return !this.horizontal;
    }

    /**
     * Getter for the shipLength attribute
     * @return The value of the shipLength attribute (the length of the ship)
     */
    public int getShipLength() {
        return this.shipLength;
    }
}

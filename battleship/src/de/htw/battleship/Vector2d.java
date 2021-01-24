package de.htw.battleship;

/**
 * An instance of this class represents a 2 dimensional vector in space, which
 * might as well just be a point.
 * @author Michael Draga
 * @version 1.0
 */
public class Vector2d {
    int x;
    int y;

    /**
     * Generate a new Vector2d with default values (-1, -1)
     */
    public Vector2d() {
        this.x = -1;
        this.y = -1;
    }

    /**
     * Generate a new Vector2d with given values
     * @param x The x value of the Vector2d
     * @param y The y value of the Vector2d
     */
    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Checks if two Vector2d's have the same values for x and y
     * @param v1 The first Vector2d
     * @param v2 The second Vector2d
     * @return Whether both Vector2d's contain the same values for x and y
     */
    public static boolean equals(Vector2d v1, Vector2d v2) {
        return v1.x == v2.x && v1.y == v2.y;
    }
}

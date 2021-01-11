package de.htw.battleship;

public class Ship extends Vector2d {
    private int shipLength;
    private boolean horizontal;
    public Ship(int x, int y) {
        super(x, y);
    }

    public Ship(Vector2d position, int shipLength, boolean horizontal) {
        super();
        this.x = position.x;
        this.y = position.y;
        this.shipLength = shipLength;
        this.horizontal = horizontal;
    }

    public Ship(int x, int y, int shipLength, boolean horizontal) {
        super(x, y);
        this.shipLength = shipLength;
        this.horizontal = horizontal;
    }

    public boolean isHorizontal() {
        return this.horizontal;
    }

    public boolean isVertical() {
        return !this.horizontal;
    }

    public int getShipLength() {
        return this.shipLength;
    }
}

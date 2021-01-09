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

    public AI(int level, Board playerBoard) {
        this.level = level;
        this. playerBoard = playerBoard;
    }

    public int[] nextMove() {
        switch (this.level) {
            case 0:
                return level0Algorithm();
            case 1:
                return level1Algorithm();
            case 2:
                return level2Algorithm();
            case 3:
                break;
            case 4:
                return level4Algorithm();
        }
        return new int[2];
    }

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

    private int[] level1Algorithm() {
        return new int[2];
    }

    private int[] level2Algorithm() {
        return new int[2];
    }

    private int[] level4Algorithm() {
        if (lastMoves.isEmpty()) {
            Random r = new Random();
            int x, y;
            do {
                x = r.nextInt(Board.BOARD_SIZE);
                y = r.nextInt(Board.BOARD_SIZE);
            } while (this.playerBoard.getField(x, y) == Board.HIT || this.playerBoard.getField(x, y) == Board.MISSED_SHOT);
            if (this.playerBoard.getField(x, y) == Board.SHIP)
                lastMoves.add(new int[] {x, y});
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
                    this.testedDifferences.add(new int[] {dx, dy});
            } while (listContainsArray(this.testedDifferences, new int[] {dx, dy}));
            this.testedDifferences.add(new int[] {dx, dy});
            int[] move = new int[] {position[0] + dx,
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
            return this.resetAndTurnAround();
        }
        this.lastMoves.add(move);
        return move;
    }

    private boolean listContainsArray(ArrayList<int[]> testedDifferences, int[] newDifference) {
        for (int[] testedDifference :
                testedDifferences) {
            if (testedDifference != null) {
                if (Arrays.equals(testedDifference, newDifference))
                    return true;
            }
        }
        return false;
    }

    private int[] advance(int[] move) {
        return new int[] {move[0] + (this.horizontal ? 1 : 0) * this.direction,
                            move[1] + (!this.horizontal ? 1 : 0) * this.direction};
    }

    private static boolean collidesWithBorder(int point) {
        return point < 0 || point >= Board.BOARD_SIZE;
    }

    private boolean hitsAgain(int[] move) {
        return this.playerBoard.getField(move[0], move[1]) == Board.MISSED_SHOT ||
                this.playerBoard.getField(move[0], move[1]) == Board.HIT;
    }

    private boolean hitsShip(int[] move) {
        return this.playerBoard.getField(move[0], move[1]) == Board.SHIP;
    }

    private int[] resetAndTurnAround() {
            this.direction *= -1;
            int[] startingPoint = lastMoves.get(0);
            this.lastMoves.clear();
            this.lastMoves.add(startingPoint);
            int[] move = advance(startingPoint);
            this.lastMoves.add(move);
            return move;
        }

    public void loseMemory() {
        this.horizontal = null;
        this.direction = 0;
        this.testedDifferences.clear();
        this.lastMoves.clear();
    }
}

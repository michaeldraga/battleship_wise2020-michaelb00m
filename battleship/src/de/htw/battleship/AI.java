package de.htw.battleship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class AI {
    private Boolean horizontal;
    private int direction;
    private ArrayList<int[]> lastMoves = new ArrayList<>();
    private ArrayList<int[]> testedDifferences = new ArrayList<>();
    private int level = 2;

    public AI(int level) {
        this.level = level;
    }

    public int[] nextMove(Board playerBoard) {
        if (lastMoves.isEmpty()) {
            Random r = new Random();
            int x, y;
            do {
                x = r.nextInt(Board.BOARD_SIZE);
                y = r.nextInt(Board.BOARD_SIZE);
            } while (playerBoard.getField(x, y) == Board.HIT || playerBoard.getField(x, y) == Board.MISSED_SHOT);
            if (playerBoard.getField(x, y) == Board.SHIP)
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
                        playerBoard.getField(position[0] + dx, position[1] + dy) == Board.MISSED_SHOT)
                    this.testedDifferences.add(new int[] {dx, dy});
            } while (listContainsArray(this.testedDifferences, new int[] {dx, dy}));
            this.testedDifferences.add(new int[] {dx, dy});
            int[] move = new int[] {position[0] + dx,
                                    position[1] + dy};
            if (hitsShip(move, playerBoard)) {
                this.lastMoves.add(move);
                this.horizontal = dx % 2 != 0;
                int sign = Integer.signum(this.horizontal ? dx : dy);
                this.direction = sign == 0 ? 1 : sign;
            }
            return move;
        }
        int[] move = advance(this.lastMoves.get(lastMoves.size() - 1));
        // TODO fix potential move detection
        if (collidesWithBorder(move[0]) ||
                collidesWithBorder(move[1]) ||
                hitsAgain(move, playerBoard)) {
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

    private boolean hitsAgain(int[] move, Board board) {
        return board.getField(move[0], move[1]) == Board.MISSED_SHOT ||
                board.getField(move[0], move[1]) == Board.HIT;
    }

    private boolean hitsShip(int[] move, Board board) {
        return board.getField(move[0], move[1]) == Board.SHIP;
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

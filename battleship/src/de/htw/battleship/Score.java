package de.htw.battleship;

/**
 * An instance of this class represents a score, holds the name of the player
 * and number of shots fired by the player during a game and can be exported
 * as a string.
 * @author Michael Draga
 * @version 1.0
 */
public class Score {
    private final String playerName;
    private final int score;

    /**
     * Generate a new Score object given a player name and their score
     * @param playerName The name of the player
     * @param score The score the player got
     */
    public Score(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    /**
     * Getter for the attribute playerName
     * @return The value of the attribute playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Getter for the attribute score
     * @return The value of the attribute score
     */
    public int getScore() {
        return score;
    }

    /**
     * Converts a Score object into a string (for saving)
     * @return The string representation of the Score object
     */
    public String toString() {
        return String.format("%s;%d", this.playerName, this.score);
    }
}

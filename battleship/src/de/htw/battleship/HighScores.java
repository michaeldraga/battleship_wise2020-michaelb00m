package de.htw.battleship;

import java.util.ArrayList;

/**
 * An instance of this class represents a high score list and is basically
 * just a wrapper for an ArrayList of type Score with the possibility to
 * add items, keep the List at a certain length, print the High Score List
 * to the console and export the High Score List as a string.
 * @author Michael Draga
 * @version 1.0
 */
public class HighScores {
    private ArrayList<Score> highScores = new ArrayList<>();

    /**
     * Generate a new empty HighScores object
     */
    public HighScores() { }

    /**
     * Generate a new HighScores object based on a saved high score list
     * @param savedHighScores The saved string representation of the HighScores object
     */
    public HighScores(ArrayList<Score> savedHighScores) {
        this.highScores = savedHighScores;
    }

    /**
     * Adds a new Score object to the high score list if the score is good enough.
     * @param newScore The new score to be added
     * @return Whether the score could be added or not
     */
    public boolean add(Score newScore) {
        for (int i = 0; i < highScores.size(); i++) {
            if (highScores.get(i).getScore() > newScore.getScore() ||
                highScores.get(i).getScore() == newScore.getScore()) {
                this.highScores.add(i, newScore);
                this.truncate();
                return true;
            }
        }
        if (highScores.size() < 10) {
            this.highScores.add(newScore);
            return true;
        }
        return false;
    }

    /**
     * Removes the last element from the high score list if it contains more
     * than 10 elements
     */
    public void truncate() {
        if (this.highScores.size() > 10)
            this.highScores.remove(this.highScores.size() - 1);
    }

    /**
     * Prints the high score list to the user
     */
    public void print() {
        System.out.println("High Scores:");
        System.out.println("Place    Score    Name");
        for (int i = 0; i < highScores.size(); i++) {
            System.out.printf("%d        %d        %s%n", i + 1, highScores.get(i).getScore(), highScores.get(i).getPlayerName());
        }
        System.out.println("\n");
    }

    /**
     * Converts the high score list to a string (for saving)
     * @return The string representation of the HighScores object
     */
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Score score :
                highScores) {
            stringBuilder.append(score.toString())
                         .append("\n");
        }
        return stringBuilder.toString();
    }
}

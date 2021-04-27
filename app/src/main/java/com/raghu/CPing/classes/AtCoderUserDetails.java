package com.raghu.CPing.classes;

public class AtCoderUserDetails {
    private int currentRating, highestRating, currentRank;
    private String currentLevel;

    public AtCoderUserDetails(int currentRating, int highestRating, int currentRank, String currentLevel) {
        this.currentRating = currentRating;
        this.highestRating = highestRating;
        this.currentRank = currentRank;
        this.currentLevel = currentLevel;
    }

    public int getCurrentRating() {
        return currentRating;
    }

    public void setCurrentRating(int currentRating) {
        this.currentRating = currentRating;
    }

    public int getHighestRating() {
        return highestRating;
    }

    public void setHighestRating(int highestRating) {
        this.highestRating = highestRating;
    }

    public int getCurrentRank() {
        return currentRank;
    }

    public void setCurrentRank(int currentRank) {
        this.currentRank = currentRank;
    }

    public String getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(String currentLevel) {
        this.currentLevel = currentLevel;
    }
}

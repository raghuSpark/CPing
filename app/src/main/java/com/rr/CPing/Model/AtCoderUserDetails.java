package com.rr.CPing.Model;

import java.util.ArrayList;

public class AtCoderUserDetails {
    private String userName;
    private int currentRating, highestRating, currentRank;
    private String currentLevel;
    private ArrayList<Integer> recentContestRatings;

    public AtCoderUserDetails(String userName, int currentRating, int highestRating, int currentRank, String currentLevel, ArrayList<Integer> recentContestRatings) {
        this.userName = userName;
        this.currentRating = currentRating;
        this.highestRating = highestRating;
        this.currentRank = currentRank;
        this.currentLevel = currentLevel;
        this.recentContestRatings = recentContestRatings;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public ArrayList<Integer> getRecentContestRatings() {
        return recentContestRatings;
    }

    public void setRecentContestRatings(ArrayList<Integer> recentContestRatings) {
        this.recentContestRatings = recentContestRatings;
    }
}

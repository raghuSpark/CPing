package com.rr.CPing.Model;

import java.util.ArrayList;

public class AtCoderUserDetails {
    private final int currentRating;
    private final int highestRating;
    private final int currentRank;
    private final String currentLevel;
    private final ArrayList<Integer> recentContestRatings;
    private String userName;

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

    public int getHighestRating() {
        return highestRating;
    }

    public int getCurrentRank() {
        return currentRank;
    }

    public String getCurrentLevel() {
        return currentLevel;
    }

    public ArrayList<Integer> getRecentContestRatings() {
        return recentContestRatings;
    }

}

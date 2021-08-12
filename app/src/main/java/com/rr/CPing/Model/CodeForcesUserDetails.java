package com.rr.CPing.Model;

import java.util.ArrayList;

public class CodeForcesUserDetails {
    private String userName;
    private final int currentRating;
    private final int maxRating;
    private final String currentRank;
    private final String maxRank;
    private final ArrayList<Integer> recentContestRatings;

    public CodeForcesUserDetails(String userName, int currentRating, int maxRating, String currentRank, String maxRank, ArrayList<Integer> recentContestRatings) {
        this.userName = userName;
        this.currentRating = currentRating;
        this.maxRating = maxRating;
        this.currentRank = currentRank;
        this.maxRank = maxRank;
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

    public int getMaxRating() {
        return maxRating;
    }

    public String getCurrentRank() {
        return currentRank;
    }

    public String getMaxRank() {
        return maxRank;
    }

    public ArrayList<Integer> getRecentContestRatings() {
        return recentContestRatings;
    }

}

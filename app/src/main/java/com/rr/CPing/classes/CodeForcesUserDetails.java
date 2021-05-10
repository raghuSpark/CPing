package com.rr.CPing.classes;

import java.util.ArrayList;

public class CodeForcesUserDetails {
    private int currentRating, maxRating;
    private String currentRank, maxRank;
    private ArrayList<String> recentContestRatings;

    public CodeForcesUserDetails(int currentRating, int maxRating, String currentRank, String maxRank, ArrayList<String> recentContestRatings) {
        this.currentRating = currentRating;
        this.maxRating = maxRating;
        this.currentRank = currentRank;
        this.maxRank = maxRank;
        this.recentContestRatings = recentContestRatings;
    }

    public int getCurrentRating() {
        return currentRating;
    }

    public void setCurrentRating(int currentRating) {
        this.currentRating = currentRating;
    }

    public int getMaxRating() {
        return maxRating;
    }

    public void setMaxRating(int maxRating) {
        this.maxRating = maxRating;
    }

    public String getCurrentRank() {
        return currentRank;
    }

    public void setCurrentRank(String currentRank) {
        this.currentRank = currentRank;
    }

    public String getMaxRank() {
        return maxRank;
    }

    public void setMaxRank(String maxRank) {
        this.maxRank = maxRank;
    }

    public ArrayList<String> getRecentContestRatings() {
        return recentContestRatings;
    }

    public void setRecentContestRatings(ArrayList<String> recentContestRatings) {
        this.recentContestRatings = recentContestRatings;
    }
}

package com.rr.CPing.Model;

import java.util.ArrayList;

public class CodeChefUserDetails {
    private String userName;
    private final int currentRating;
    private final int highestRating;
    private final String currentStars;
    private final ArrayList<Integer> recentContestRatings;

    public CodeChefUserDetails(String userName, int currentRating, int highestRating, String currentStars, ArrayList<Integer> recentContestRatings) {
        this.userName = userName;
        this.currentRating = currentRating;
        this.highestRating = highestRating;
        this.currentStars = currentStars;
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

    public String getCurrentStars() {
        return currentStars;
    }

    public ArrayList<Integer> getRecentContestRatings() {
        return recentContestRatings;
    }

}

package com.rr.CPing.classes;

import java.util.ArrayList;

public class CodeChefUserDetails {
    private int currentRating,highestRating;
    private String currentStars;
    private ArrayList<Integer> recentContestRatings;

    public CodeChefUserDetails(int currentRating, int highestRating, String currentStars, ArrayList<Integer> recentContestRatings) {
        this.currentRating = currentRating;
        this.highestRating = highestRating;
        this.currentStars = currentStars;
        this.recentContestRatings = recentContestRatings;
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

    public String getCurrentStars() {
        return currentStars;
    }

    public void setCurrentStars(String currentStars) {
        this.currentStars = currentStars;
    }

    public ArrayList<Integer> getRecentContestRatings() {
        return recentContestRatings;
    }

    public void setRecentContestRatings(ArrayList<Integer> recentContestRatings) {
        this.recentContestRatings = recentContestRatings;
    }
}

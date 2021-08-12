package com.rr.CPing.Model;

public class LeetCodeUserDetails {
    private String userName;
    private final String totalProblemsSolved;
    private final String acceptance_rate;
    private final String easySolved;
    private final String totalEasy;
    private final String mediumSolved;
    private final String totalMedium;
    private final String hardSolved;
    private final String totalHard;

    public LeetCodeUserDetails(String userName, String totalProblemsSolved, String acceptance_rate, String easySolved, String totalEasy, String mediumSolved, String totalMedium, String hardSolved, String totalHard) {
        this.userName = userName;
        this.totalProblemsSolved = totalProblemsSolved;
        this.acceptance_rate = acceptance_rate;
        this.easySolved = easySolved;
        this.totalEasy = totalEasy;
        this.mediumSolved = mediumSolved;
        this.totalMedium = totalMedium;
        this.hardSolved = hardSolved;
        this.totalHard = totalHard;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTotalProblemsSolved() {
        return totalProblemsSolved;
    }

    public String getAcceptance_rate() {
        return acceptance_rate;
    }

    public String getEasySolved() {
        return easySolved;
    }

    public String getTotalEasy() {
        return totalEasy;
    }

    public String getMediumSolved() {
        return mediumSolved;
    }

    public String getTotalMedium() {
        return totalMedium;
    }

    public String getHardSolved() {
        return hardSolved;
    }

    public String getTotalHard() {
        return totalHard;
    }

}

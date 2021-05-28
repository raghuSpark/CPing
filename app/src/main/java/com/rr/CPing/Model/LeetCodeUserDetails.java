package com.rr.CPing.Model;

public class LeetCodeUserDetails {
    private String userName, totalProblemsSolved, acceptance_rate, easySolved, totalEasy,
            mediumSolved, totalMedium, hardSolved, totalHard;

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

    public void setTotalProblemsSolved(String totalProblemsSolved) {
        this.totalProblemsSolved = totalProblemsSolved;
    }

    public String getAcceptance_rate() {
        return acceptance_rate;
    }

    public void setAcceptance_rate(String acceptance_rate) {
        this.acceptance_rate = acceptance_rate;
    }

    public String getEasySolved() {
        return easySolved;
    }

    public void setEasySolved(String easySolved) {
        this.easySolved = easySolved;
    }

    public String getTotalEasy() {
        return totalEasy;
    }

    public void setTotalEasy(String totalEasy) {
        this.totalEasy = totalEasy;
    }

    public String getMediumSolved() {
        return mediumSolved;
    }

    public void setMediumSolved(String mediumSolved) {
        this.mediumSolved = mediumSolved;
    }

    public String getTotalMedium() {
        return totalMedium;
    }

    public void setTotalMedium(String totalMedium) {
        this.totalMedium = totalMedium;
    }

    public String getHardSolved() {
        return hardSolved;
    }

    public void setHardSolved(String hardSolved) {
        this.hardSolved = hardSolved;
    }

    public String getTotalHard() {
        return totalHard;
    }

    public void setTotalHard(String totalHard) {
        this.totalHard = totalHard;
    }
}

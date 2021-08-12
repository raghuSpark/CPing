package com.rr.CPing.Model;

public class HiddenContestsClass {
    private String contestName, platformName;
    private final long contestEndTime;

    public HiddenContestsClass(String contestName, String platformName, long contestEndTime) {
        this.contestName = contestName;
        this.platformName = platformName;
        this.contestEndTime = contestEndTime;
    }

    public String getContestName() {
        return contestName;
    }

    public void setContestName(String contestName) {
        this.contestName = contestName;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public long getContestEndTime() {
        return contestEndTime;
    }

}

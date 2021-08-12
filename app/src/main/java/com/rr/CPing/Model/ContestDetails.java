package com.rr.CPing.Model;

public class ContestDetails {
    private String site;
    private String contestName;
    private final String contestUrl;
    private final int contestDuration;
    private final String contestStartTime;
    private final String contestEndTime;
    private String isToday;
    private final String contestStatus;

    public ContestDetails(String site, String contestName, String contestUrl, int contestDuration, String contestStartTime, String contestEndTime, String isToday, String contestStatus) {
        this.site = site;
        this.contestName = contestName;
        this.contestUrl = contestUrl;
        this.contestDuration = contestDuration;
        this.contestStartTime = contestStartTime;
        this.contestEndTime = contestEndTime;
        this.isToday = isToday;
        this.contestStatus = contestStatus;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getContestName() {
        return contestName;
    }

    public void setContestName(String contestName) {
        this.contestName = contestName;
    }

    public String getContestUrl() {
        return contestUrl;
    }

    public int getContestDuration() {
        return contestDuration;
    }

    public String getContestStartTime() {
        return contestStartTime;
    }

    public String getContestEndTime() {
        return contestEndTime;
    }

    public String getIsToday() {
        return isToday;
    }

    public void setIsToday(String isToday) {
        this.isToday = isToday;
    }

    public String getContestStatus() {
        return contestStatus;
    }

}

package com.rr.CPing.classes;

public class ContestDetails {
    private String site, contestName, contestUrl;
    private int contestDuration;
    private String contestStartTime, contestEndTime, isToday, contestStatus;

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

    public void setContestUrl(String contestUrl) {
        this.contestUrl = contestUrl;
    }

    public int getContestDuration() {
        return contestDuration;
    }

    public void setContestDuration(int contestDuration) {
        this.contestDuration = contestDuration;
    }

    public String getContestStartTime() {
        return contestStartTime;
    }

    public void setContestStartTime(String contestStartTime) {
        this.contestStartTime = contestStartTime;
    }

    public String getContestEndTime() {
        return contestEndTime;
    }

    public void setContestEndTime(String contestEndTime) {
        this.contestEndTime = contestEndTime;
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

    public void setContestStatus(String contestStatus) {
        this.contestStatus = contestStatus;
    }
}

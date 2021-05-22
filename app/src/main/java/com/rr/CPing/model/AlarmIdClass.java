package com.rr.CPing.model;

public class AlarmIdClass {
    private String contestNameAsID;
    private long startTime, alarmSetTime;

    public AlarmIdClass(String contestNameAsID, long startTime, long alarmSetTime) {
        this.contestNameAsID = contestNameAsID;
        this.startTime = startTime;
        this.alarmSetTime = alarmSetTime;
    }

    public String getContestNameAsID() {
        return contestNameAsID;
    }

    public void setContestNameAsID(String contestNameAsID) {
        this.contestNameAsID = contestNameAsID;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getAlarmSetTime() {
        return alarmSetTime;
    }

    public void setAlarmSetTime(long alarmSetTime) {
        this.alarmSetTime = alarmSetTime;
    }
}

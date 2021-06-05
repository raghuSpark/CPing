package com.rr.CPing.Model;

public class AlarmIdClass {
    private String contestNameAsID;
    private long startTime, alarmSetTime;
    private int spinnerPosition;

    public AlarmIdClass(String contestNameAsID, long startTime, long alarmSetTime, int spinnerPosition) {
        this.contestNameAsID = contestNameAsID;
        this.startTime = startTime;
        this.alarmSetTime = alarmSetTime;
        this.spinnerPosition = spinnerPosition;
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

    public int getSpinnerPosition() {
        return spinnerPosition;
    }

    public void setSpinnerPosition(int spinnerPosition) {
        this.spinnerPosition = spinnerPosition;
    }
}

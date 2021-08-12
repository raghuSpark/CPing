package com.rr.CPing.Model;

public class AlarmIdClass {
    private final String contestNameAsID;
    private final long startTime;
    private final String properStartTime;
    private long alarmSetTime;
    private int spinnerPosition;

    public AlarmIdClass(String contestNameAsID, long startTime, long alarmSetTime, int spinnerPosition, String properStartTime) {
        this.contestNameAsID = contestNameAsID;
        this.startTime = startTime;
        this.alarmSetTime = alarmSetTime;
        this.spinnerPosition = spinnerPosition;
        this.properStartTime = properStartTime;
    }

    public String getContestNameAsID() {
        return contestNameAsID;
    }

    public long getStartTime() {
        return startTime;
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

    public String getProperStartTime() {
        return properStartTime;
    }

}

package com.rr.CPing.Model;

public class PlatformListItem {
    private String platformName, userName;
    private boolean isUserNameAllowed, isEnabled;

    public PlatformListItem(String platformName, String userName, boolean isUserNameAllowed, boolean isEnabled) {
        this.platformName = platformName;
        this.userName = userName;
        this.isUserNameAllowed = isUserNameAllowed;
        this.isEnabled = isEnabled;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isUserNameAllowed() {
        return isUserNameAllowed;
    }

    public void setUserNameAllowed(boolean userNameAllowed) {
        isUserNameAllowed = userNameAllowed;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
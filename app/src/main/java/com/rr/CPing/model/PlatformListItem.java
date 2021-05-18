package com.rr.CPing.model;

public class PlatformListItem {
    private final String platformName;
    private final boolean isUserNameAllowed;
    private String userName;
    private boolean isEnabled;
    private int logo, logo2X;

    public PlatformListItem(String platformName, String userName, boolean isEnabled, boolean isUserNameAllowed, int logo, int logo2X) {
        this.platformName = platformName;
        this.userName = userName;
        this.isEnabled = isEnabled;
        this.isUserNameAllowed = isUserNameAllowed;
        this.logo = logo;
        this.logo2X = logo2X;
    }

    public String getPlatformName() {
        return platformName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isUserNameAllowed() {
        return isUserNameAllowed;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public int getLogo2X() {
        return logo2X;
    }

    public void setLogo2X(int logo2X) {
        this.logo2X = logo2X;
    }
}
package com.raghu.CPing.classes;

public class PlatformListItem {
    private final String platformName;
    private String username;
    private boolean isEnabled;
    private final boolean isUsernameAllowed;

    public PlatformListItem(String platformName, String username, boolean isEnabled, boolean isUsernameAllowed) {
        this.platformName = platformName;
        this.username = username;
        this.isEnabled = isEnabled;
        this.isUsernameAllowed = isUsernameAllowed;
    }

    public String getPlatformName() {
        return platformName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isUsernameAllowed() {
        return isUsernameAllowed;
    }
}
package com.raghu.CPing.classes;

import java.util.ArrayList;

public class PlatformDetails {
    private String platformName;
    private ArrayList<ContestDetails> platformContests;

    public PlatformDetails(String platformName, ArrayList<ContestDetails> platformContests) {
        this.platformName = platformName;
        this.platformContests = platformContests;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public ArrayList<ContestDetails> getPlatformContests() {
        return platformContests;
    }

    public void setPlatformContests(ArrayList<ContestDetails> platformContests) {
        this.platformContests = platformContests;
    }
}

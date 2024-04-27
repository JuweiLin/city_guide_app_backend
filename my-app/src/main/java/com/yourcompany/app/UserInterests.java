package com.yourcompany.app;

import java.util.Map;

public class UserInterests {
    private String uid;
    private Map<String, Double> interests;

    public UserInterests() {}

    public UserInterests(String uid, Map<String, Double> interests) {
        this.uid = uid;
        this.interests = interests;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Map<String, Double> getInterests() {
        return interests;
    }

    public void setInterests(Map<String, Double> interests) {
        this.interests = interests;
    }
}

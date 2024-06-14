package com.distrelec.smartedit.populator.data;

public class SmarteditSiteData {
    public static final String DISPLAY_ON_SITES_KEY = "displayOnSites";

    private String uid;
    private String name;

    public SmarteditSiteData(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

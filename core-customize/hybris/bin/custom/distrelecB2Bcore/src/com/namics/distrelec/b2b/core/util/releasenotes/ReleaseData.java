/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util.releasenotes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * save data between generation an print
 * 
 * @author rhaemmerli, Namics AG
 * @since Namics Extensions 1.0
 * 
 */

public class ReleaseData {

    private String svnInfoPath;

    private String svnBranchesPath;

    private Map<String, List<JiraData>> jiras;

    private Map<String, JiraData> jiraLimitations;

    private List<SvnData> svnNoJira;

    private List<SvnData> svnNoJiraInRelease;

    public String getSvnInfoPath() {
        return svnInfoPath;
    }

    public void setSvnInfoPath(final String svnInfoPath) {
        this.svnInfoPath = svnInfoPath;
    }

    public String getSvnBranchesPath() {
        return svnBranchesPath;
    }

    public void setSvnBranchesPath(final String svnBranchesPath) {
        this.svnBranchesPath = svnBranchesPath;
    }

    public Map<String, List<JiraData>> getJiras() {
        return jiras;
    }

    public void setJiras(final Map<String, List<JiraData>> jiras) {
        this.jiras = jiras;
    }

    public Map<String, JiraData> getJiraLimitations() {
        return jiraLimitations;
    }

    public void setJiraLimitations(final Map<String, JiraData> jiraLimitations) {
        this.jiraLimitations = jiraLimitations;
    }

    public List<SvnData> getSvnNoJira() {
        return svnNoJira;
    }

    public void setSvnNoJira(final List<SvnData> svnNoJira) {
        this.svnNoJira = svnNoJira;
    }

    public List<SvnData> getSvnNoJiraInRelease() {
        return svnNoJiraInRelease;
    }

    public void setSvnNoJiraInRelease(final List<SvnData> svnNoJiraInRelease) {
        this.svnNoJiraInRelease = svnNoJiraInRelease;
    }

}

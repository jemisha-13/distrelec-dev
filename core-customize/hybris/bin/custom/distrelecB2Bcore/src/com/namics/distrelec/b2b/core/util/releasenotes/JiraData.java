/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util.releasenotes;

import java.util.ArrayList;
import java.util.List;

/**
 * jirs data holder
 * 
 * @author rhaemmerli, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class JiraData {

    private String id;

    private String url;

    private String title;

    private String status;

    private String type;

    private String resolution;
    private String reporter;
    private String assignee;
    
    private String fixVersion;

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    private final List<SvnData> svnEntries = new ArrayList<SvnData>();

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public List<SvnData> getSvnEntries() {
        return svnEntries;
    }

    public void addSvnEntry(final SvnData data) {
        this.svnEntries.add(data);
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public boolean isBug() {
        return "Bug".equals(type);
    }

    public boolean isHassvn() {
        return !svnEntries.isEmpty();
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(final String resolution) {
        this.resolution = resolution;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(final String reporter) {
        this.reporter = reporter;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(final String assignee) {
        this.assignee = assignee;
    }

    public String getFixVersion() {
        return fixVersion;
    }

    public void setFixVersion(final String fixVersion) {
        this.fixVersion = fixVersion;
    }

}

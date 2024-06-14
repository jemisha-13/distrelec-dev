/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util.releasenotes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class SvnData {

    private String id;

    private String jiraId;

    private String comment;

    private String comitter;

    private final List<String> files = new ArrayList<String>();

    public String getComitter() {
        return comitter;
    }

    public void setComitter(final String comitter) {
        this.comitter = comitter;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getJiraId() {
        return jiraId;
    }

    public void setJiraId(final String jiraId) {
        this.jiraId = jiraId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public List<String> getFiles() {
        return files;
    }

    public void addFile(final String file) {
        this.files.add(file);
    }
    
    public boolean hasJira()
    {
        return !StringUtils.isEmpty(getJiraId());
    }

}

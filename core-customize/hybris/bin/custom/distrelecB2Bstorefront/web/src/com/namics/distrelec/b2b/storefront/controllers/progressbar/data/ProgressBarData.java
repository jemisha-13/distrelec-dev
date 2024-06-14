/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.progressbar.data;

import java.util.List;

public class ProgressBarData {

    private String code;
    private boolean active;
    private String pageUrl;
    private List<String> pageUrls;
    private String url;
    private String stepClass;
    private String messageCode;

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getStepClass() {
        return stepClass;
    }

    public void setStepClass(final String stepClass) {
        this.stepClass = stepClass;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(final String messageCode) {
        this.messageCode = messageCode;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(final String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public List<String> getPageUrls() {
        return pageUrls;
    }

    public void setPageUrls(final List<String> pageUrls) {
        this.pageUrls = pageUrls;
    }
}

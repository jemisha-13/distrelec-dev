/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap;

public class SitemapUrlData {

    private String entity;
    private String url;
    private String lastModified;
    private String code;
    private String language;
    private String defaultLanguage;

    public String getEntity() {
        return entity;
    }

    public void setEntity(final String entity) {
        this.entity = entity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(final String lastModified) {
        this.lastModified = lastModified;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(final String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

}

/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator;

public class WebSitemapLink {
    private String rel;
    private String hreflang;
    private String href;
    private String language;

    public String getRel() {
        return rel;
    }

    public void setRel(final String rel) {
        this.rel = rel;
    }

    public String getHreflang() {
        return hreflang;
    }

    public void setHreflang(final String hreflang) {
        this.hreflang = hreflang;
    }

    public String getHref() {
        return href;
    }

    public void setHref(final String href) {
        this.href = href;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

}

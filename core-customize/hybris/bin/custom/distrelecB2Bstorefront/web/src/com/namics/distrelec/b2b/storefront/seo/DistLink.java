/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.storefront.seo;

import java.io.Serializable;

/**
 * {@code DistLink}
 * <p>
 * Utility class to represent a link. A link can be a header link, footer link or a media link. This class provides the following
 * information:
 * <ul>
 * <li>rel: the link type, i.e., canonical, alternate, etc.</li>
 * <li>href: the link URL</li>
 * <li>hreflang: the link language</li>
 * <li>countryName: the country name</li>
 * <li>mediaQuery</li>
 * </ul>
 * </p>
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.8
 */
public class DistLink implements Serializable {

    private String rel;
    private String href;
    private String hreflang;
    private String countryName;
    private String mediaQuery;
    private LinkType type = LinkType.ALL;

    /**
     * Create a new instance of {@code DistLink}
     */
    public DistLink() {
        super();
    }

    /**
     * Create a new instance of {@code DistLink}
     * 
     * @param rel
     * @param href
     * @param hreflang
     */
    public DistLink(final String rel, final String href, final String hreflang) {
        this(rel, href, hreflang, null);
    }

    /**
     * Create a new instance of {@code DistLink}
     * 
     * @param rel
     * @param href
     * @param countryName
     * @param hreflang
     */
    public DistLink(final String rel, final String href, final String hreflang, final String countryName) {
        this.rel = rel;
        this.href = href;
        this.hreflang = hreflang;
        this.countryName = countryName;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(final String rel) {
        this.rel = rel;
    }

    public String getHref() {
        return href;
    }

    public void setHref(final String href) {
        this.href = href;
    }

    public String getHreflang() {
        return hreflang;
    }

    public void setHreflang(final String hreflang) {
        this.hreflang = hreflang;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(final String countryName) {
        this.countryName = countryName;
    }

    public String getMediaQuery() {
        return mediaQuery;
    }

    public void setMediaQuery(final String mediaQuery) {
        this.mediaQuery = mediaQuery;
    }

    public LinkType getType() {
        return type;
    }

    /**
     * Set the link type. If the {@code type} parameter is {@code null}, then {@link LinkType#ALL} is set.
     * 
     * @param type
     *            the link type to set.
     */
    public void setType(final LinkType type) {
        this.type = (type != null ? type : LinkType.ALL);
    }

    @Override
    public String toString() {
        return href;
    }
}

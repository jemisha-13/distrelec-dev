/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.storefront.seo;

/**
 * {@code LinkType}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.8
 */
public enum LinkType {
    /**
     * Header only link
     */
    HEADER,
    /**
     * Footer only Link
     */
    FOOTER,
    /**
     * Link for all
     */
    ALL;

    /**
     * @return the name of the enumeration value.
     */
    public String getCode() {
        return name();
    }
}

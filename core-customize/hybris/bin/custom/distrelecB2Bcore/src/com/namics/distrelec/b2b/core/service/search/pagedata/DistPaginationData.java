/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.search.pagedata;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;

/**
 * {@code DistPaginationData}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class DistPaginationData extends PaginationData {

    private String firstUrl;
    private String nextUrl;
    private String prevUrl;
    private String lastUrl;
    private int nextPageNr;
    private int prevPageNr;

    /**
     * Will it not make sense to increase the pageSize?
     */
    public boolean isMaxPageSizeReached() {
        return getPageSize() >= getTotalNumberOfResults();
    }

    // Getters & Setters

    public String getFirstUrl() {
        return firstUrl;
    }

    public void setFirstUrl(String firstUrl) {
        this.firstUrl = firstUrl;
    }

    public String getNextUrl() {
        return nextUrl;
    }

    public void setNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
    }

    public String getPrevUrl() {
        return prevUrl;
    }

    public void setPrevUrl(String prevUrl) {
        this.prevUrl = prevUrl;
    }

    public String getLastUrl() {
        return lastUrl;
    }

    public void setLastUrl(String lastUrl) {
        this.lastUrl = lastUrl;
    }

    public int getNextPageNr() {
        return nextPageNr;
    }

    public void setNextPageNr(int nextPageNr) {
        this.nextPageNr = nextPageNr;
    }

    public int getPrevPageNr() {
        return prevPageNr;
    }

    public void setPrevPageNr(int prevPageNr) {
        this.prevPageNr = prevPageNr;
    }
}

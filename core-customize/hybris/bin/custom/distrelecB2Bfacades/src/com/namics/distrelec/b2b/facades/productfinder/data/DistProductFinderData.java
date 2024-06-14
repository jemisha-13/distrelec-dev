/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.data;

import java.util.List;

/**
 * Product finder data.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistProductFinderData {

    private String categoryCode;
    private long totalNumberOfResults;
    private List<DistProductFinderColumnData> columns;
    private String searchUrl;

    public List<DistProductFinderColumnData> getColumns() {
        return columns;
    }

    public void setColumns(final List<DistProductFinderColumnData> columns) {
        this.columns = columns;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(final String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public long getTotalNumberOfResults() {
        return totalNumberOfResults;
    }

    public void setTotalNumberOfResults(final long totalNumberOfResults) {
        this.totalNumberOfResults = totalNumberOfResults;
    }

    public String getSearchUrl() {
        return searchUrl;
    }

    public void setSearchUrl(final String searchUrl) {
        this.searchUrl = searchUrl;
    }

}

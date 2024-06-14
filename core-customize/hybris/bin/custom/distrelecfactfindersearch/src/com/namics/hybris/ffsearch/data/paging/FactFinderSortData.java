/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.paging;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.hybris.platform.commerceservices.search.pagedata.SortData;

/**
 * POJO for sort options.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class FactFinderSortData extends SortData {

    private boolean relevanceSort;
    private SortType sortType;

    // BEGIN GENERATED CODE

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public boolean isRelevanceSort() {
        return relevanceSort;
    }

    public void setRelevanceSort(final boolean relevanceSort) {
        this.relevanceSort = relevanceSort;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(final SortType sortType) {
        this.sortType = sortType;
    }

}

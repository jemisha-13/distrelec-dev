/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.request;

import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.converters.Populator;

/**
 * Poplulator for page size and current page.
 * 
 * @author ceberle, Namics AG
 * @since Namics Extensions 1.0
 */
public class RequestPaginationPopulator implements Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> {

    private Integer defaultPageSize;

    @Override
    public void populate(final SearchQueryPageableData<SearchQueryData> source, final SearchRequest target) {
        Integer resultsPerPage = null;

        final PageableData pageableData = source.getPageableData();
        if (pageableData != null) {
            if (pageableData.getPageSize() > 0) {
                resultsPerPage = Integer.valueOf(pageableData.getPageSize());
            }
            if (pageableData.getCurrentPage() > 0) {
                target.getSearchParams().setPage(Integer.valueOf(pageableData.getCurrentPage()));
            }
        }

        if (resultsPerPage == null) {
            resultsPerPage = getDefaultPageSize();
        }

        target.getSearchParams().setResultsPerPage(resultsPerPage);
    }

    public Integer getDefaultPageSize() {
        return defaultPageSize;
    }

    @Required
    public void setDefaultPageSize(final Integer defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

}

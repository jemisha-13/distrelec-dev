package com.distrelec.fusionsearch.impl;

import com.distrelec.fusionsearch.request.SearchRequestTuple;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;

class SearchRequestTupleImpl implements SearchRequestTuple {

    private final SearchQueryPageableData<SearchQueryData> searchQueryPageableData;

    private final SearchRequest searchRequest;

    SearchRequestTupleImpl(SearchQueryPageableData<SearchQueryData> searchQueryPageableData, SearchRequest searchRequest) {
        this.searchQueryPageableData = searchQueryPageableData;
        this.searchRequest = searchRequest;
    }

    @Override
    public PageableData getPageableData() {
        return searchQueryPageableData.getPageableData();
    }

    @Override
    public SearchQueryData getSearchQueryData() {
        return searchQueryPageableData.getSearchQueryData();
    }

    @Override
    public SearchQueryPageableData<SearchQueryData> getSearchQueryPageableData() {
        return searchQueryPageableData;
    }

    @Override
    public SearchRequest getSearchRequest() {
        return searchRequest;
    }
}

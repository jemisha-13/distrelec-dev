package com.distrelec.fusionsearch.impl;

import com.distrelec.fusion.search.dto.SearchResponseDTO;
import com.distrelec.fusionsearch.response.SearchResponseTuple;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import com.namics.hybris.ffsearch.data.search.SearchRequest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;

class SearchResponseTupleImpl implements SearchResponseTuple {

    private final SearchQueryPageableData<SearchQueryData> searchQueryPageableData;

    private final SearchRequest searchRequest;
    private final SearchResponseDTO searchResponseDTO;

    public SearchResponseTupleImpl(SearchQueryPageableData<SearchQueryData> searchQueryPageableData, SearchRequest searchRequest, SearchResponseDTO searchResponseDTO) {
        this.searchQueryPageableData = searchQueryPageableData;
        this.searchRequest = searchRequest;
        this.searchResponseDTO = searchResponseDTO;
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

    @Override
    public SearchResponseDTO getSearchResponseDTO() {
        return searchResponseDTO;
    }
}

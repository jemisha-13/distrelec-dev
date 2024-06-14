package com.distrelec.fusionsearch.request;

import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;

public interface SearchRequestTuple {

    PageableData getPageableData();

    SearchQueryData getSearchQueryData();

    SearchQueryPageableData<SearchQueryData> getSearchQueryPageableData();

    SearchRequest getSearchRequest();
}

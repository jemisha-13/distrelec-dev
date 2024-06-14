package com.distrelec.fusionsearch.response;

import com.distrelec.fusion.search.dto.SearchResponseDTO;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;

public interface SearchResponseTuple {

    PageableData getPageableData();

    SearchQueryData getSearchQueryData();

    SearchQueryPageableData<SearchQueryData> getSearchQueryPageableData();

    SearchRequest getSearchRequest();

    SearchResponseDTO getSearchResponseDTO();
}

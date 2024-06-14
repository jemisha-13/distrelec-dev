package com.namics.hybris.ffsearch.populator.response;

import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;
import com.namics.hybris.ffsearch.data.search.SearchResponse;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;

public class ResponseSessionIdPopulator implements Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> {

    @Override
    public void populate(SearchResponse source, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> target) {
        SearchRequest searchReq = source.getSearchRequest();
        target.setSessionId(searchReq.getSessionId());
    }
}

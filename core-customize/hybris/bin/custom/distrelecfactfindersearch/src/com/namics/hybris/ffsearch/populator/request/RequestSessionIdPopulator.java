package com.namics.hybris.ffsearch.populator.request;

import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.converters.Populator;

public class RequestSessionIdPopulator implements Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> {

    @Override
    public void populate(SearchQueryPageableData<SearchQueryData> source, SearchRequest target) {
        SearchQueryData queryData = source.getSearchQueryData();
        target.setSessionId(queryData.getSessionId());
    }
}

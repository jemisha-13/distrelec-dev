package com.distrelec.fusionsearch.response;

import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.factfinder.webservice.ws71.FFsearch.Params;
import de.hybris.platform.converters.Populator;

/**
 * A replacement for ResponsePageDataPopulator.
 */
class EnhanceSearchQueryStrategyImpl implements EnhanceSearchQueryStrategy {

    private Populator<Params, SearchQueryData> queryDataPopulator;

    @Override
    public SearchQueryData enhanceSearchQuery(SearchResponseTuple searchResponseTuple) {
        SearchQueryData enhancedSearchQuery = new SearchQueryData();

        SearchRequest searchRequest = searchResponseTuple.getSearchRequest();
        Params params = searchRequest.getSearchParams();
        queryDataPopulator.populate(params, enhancedSearchQuery);

        SearchQueryData searchQuery = searchResponseTuple.getSearchQueryData();
        enhancedSearchQuery.setCode(searchQuery.getCode());
        enhancedSearchQuery.setSearchType(searchQuery.getSearchType());

        return enhancedSearchQuery;
    }

    public void setQueryDataPopulator(Populator<Params, SearchQueryData> queryDataPopulator) {
        this.queryDataPopulator = queryDataPopulator;
    }
}

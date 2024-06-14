/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.response;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchResponse;

import de.factfinder.webservice.ws71.FFsearch.Params;
import de.hybris.platform.converters.Populator;

/**
 * Populator for the query, category code and filter terms.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 * @param <ITEM>
 */
public class ResponsePageDataPopulator<ITEM> implements Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> {

    private Populator<Params, SearchQueryData> queryDataPopulator;

    @Override
    public void populate(final SearchResponse source, final FactFinderProductSearchPageData<SearchQueryData, ITEM> target) {
        Assert.notNull(source.getSearchRequest());
        Assert.notNull(source.getSearchRequest().getSearchParams());
        final SearchQueryData query = buildSearchQueryData(source);
        target.setFreeTextSearch(query.getFreeTextSearch());
        target.setSearchType(query.getSearchType());
        target.setCode(query.getCode());
        if (source.getSearchResult() != null) {
            target.setResultStatus(source.getSearchResult().getResultStatus());
            target.setResultArticleNumberStatus(source.getSearchResult().getResultArticleNumberStatus());
        }

        target.setCurrentQuery(query);
        target.setTechnicalView(source.isTechnicalView());
        target.setTimedOut(source.isTimedOut());
    }

    protected SearchQueryData buildSearchQueryData(final SearchResponse source) {
        final Params searchParams = source.getSearchRequest().getSearchParams();
        final SearchQueryData result = createSearchQueryData();

        getQueryDataPopulator().populate(searchParams, result);
        result.setSearchType(source.getSearchRequest().getSearchType());
        result.setCode(source.getSearchRequest().getCode());

        // On manufacturer searches the free text search is modified by RequestManufacturerPopulator.
        // Therefore the free text search is set back to "*" to get a proper URL.
        if (isManufacturerOrOutletOrNewSearch(source.getSearchRequest().getSearchType())) {
            result.setFreeTextSearch("*");
        }

        return result;
    }

    private boolean isManufacturerOrOutletOrNewSearch(final DistSearchType searchTye) {
        return DistSearchType.MANUFACTURER.equals(searchTye) || DistSearchType.OUTLET.equals(searchTye) || DistSearchType.NEW.equals(searchTye);
    }

    private SearchQueryData createSearchQueryData() {
        return new SearchQueryData();
    }

    // / BEGIN GENERATED CODE

    protected Populator<Params, SearchQueryData> getQueryDataPopulator() {
        return queryDataPopulator;
    }

    @Required
    public void setQueryDataPopulator(final Populator<Params, SearchQueryData> queryDataPopulator) {
        this.queryDataPopulator = queryDataPopulator;
    }
}

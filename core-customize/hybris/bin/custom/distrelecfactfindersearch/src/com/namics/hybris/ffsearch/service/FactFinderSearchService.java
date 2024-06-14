/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service;

import java.util.List;
import java.util.Map;

import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

/**
 * ProductSearchService interface. Its main purpose is to retrieve product search results. The search service implementation is stateless,
 * i.e. it does not maintain any state for each search, instead it externalizes the search state in the search page data returned.
 * 
 * @param <STATE>
 *            The type of the search query state. This is implementation specific. For example {@link SearchQueryData}
 * @param <ITEM>
 *            The type of items returned as part of the search results. For example
 *            {@link de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData}
 * @param <RESULT>
 *            The type of the search page data returned. Must be (or extend)
 *            {@link com.namics.hybris.ffsearch.data.facet.FactFinderFacetSearchPageData}.
 * @param <PAGING>
 *            The type of items returned as part of a pagination search. This is implementation specific. For example
 *            {@link com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData}
 */
public interface FactFinderSearchService<STATE, ITEM, RESULT extends FactFinderProductSearchPageData<STATE, ITEM>, PAGING> {

    /**
     * Executes a FactFinder search.
     * 
     * @param searchQueryData
     *            the search query object
     * @param pageableData
     *            the page to return
     * @param generateASN
     *            whether the search response should contain "After Search Navigation" information
     * @param disableWebUseAttributeCount
     *            whether the webuse attribute count should be returned
     * @param log
     *            to which log file should this request be logged. If empty Fact Finder will decide which is the right log file.
     * @param otherSearchParams
     *            other search parameter that get added to the search parameters. If the parameter is not known by FF it get returned as it
     *            is.
     * @return the search results
     */
    RESULT search(STATE searchQueryData, PageableData pageableData, final boolean disableWebUseAttributeCount, boolean generateASN, final String log,
            final Map<String, List<String>> otherSearchParams);

    RESULT search(STATE searchQueryData, PageableData pageableData, final boolean disableWebUseAttributeCount, boolean generateASN, final boolean idsOnly,
            final String log, final Map<String, List<String>> otherSearchParams);

    RESULT search(STATE searchQueryData, PageableData pageableData, final boolean disableWebUseAttributeCount, boolean generateASN, final boolean idsOnly,
            final boolean useCampaigns, final String log, final Map<String, List<String>> otherSearchParams);

    /**
     * Builds up pagination (for next/previous links) for a certain {@link SearchQueryData}.
     * 
     * @param searchQueryData
     *            what to build the paging for
     * @param pageableData
     *            how to build the paging
     * @param disableWebUseAttributeCount
     *            whether the webuse attribute count should be returned
     * @return the paging data
     */
    PAGING doPaginationSearch(SearchQueryData searchQueryData, PageableData pageableData, final boolean disableWebUseAttributeCount);

}

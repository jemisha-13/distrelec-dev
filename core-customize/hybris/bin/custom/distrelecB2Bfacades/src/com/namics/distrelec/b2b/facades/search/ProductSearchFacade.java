/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search;

import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.enums.SearchExperience;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.suggest.AutocompleteSuggestion;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfFilter;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

/**
 * Product search facade interface. Used to execute searches against FactFinder.
 *
 */
public interface ProductSearchFacade<ITEM extends ProductData> {

    /**
     * Returns a search experience for a current customer on an accelerator storefront.
     */
    SearchExperience getSearchExperienceFromCurrentBaseStore();

    ArrayOfFilter createCategoryRestrictionsArray(String categoryRestrictions);

    /**
     * Executes a FactFinder search with default values and returns a list of {@link FactFinderProductSearchPageData}. <br/>
     * in the end {@link #search(SearchStateData, PageableData, DistSearchType, String, boolean, boolean, String)} will be called. <br/>
     * <br/>
     * used default values: <br/>
     * - searchType: <code>TEXT</code><br/>
     * - code: <code>null</code><br/>
     * - generateASN: <code>false</code><br/>
     * - log: <code>"internal"</code><br/>
     * - disableWebUseAttributeCount: <code>false</code><br/>
     *
     * @param searchState
     *            the search query object
     * @param pageableData
     *            the page to return
     * @return a product list from FactFinder
     */
    FactFinderProductSearchPageData<SearchStateData, ITEM> search(SearchStateData searchState, PageableData pageableData);

    /**
     * Executes a FactFinder search with default values and returns a list of {@link FactFinderProductSearchPageData}. <br/>
     * in the end {@link #search(SearchStateData, PageableData, DistSearchType, String, boolean, boolean, String)} will be called. <br/>
     * <br/>
     * used default values: <br/>
     * - disableWebUseAttributeCount: <code>false</code><br/>
     *
     * @param searchState
     *            the search query object
     * @param pageableData
     *            the page to return
     * @param searchType
     *            defines the search type
     * @param code
     *            additional code for special searches (e.g. categoryCode, manufacturerCode,...)
     * @param generateASN
     *            whether the search response should contain "After Search Navigation" information
     * @param log
     *            to which log file should this request be logged. If empty Fact Finder will decide which is the right log file.
     * @param otherSearchParams
     *            other search parameter that get added to the search parameters. If the parameter is not known by FF it get returned as it
     *            is.
     * @return a product list from FactFinder
     */
    FactFinderProductSearchPageData<SearchStateData, ITEM> search(boolean tracking, SearchStateData searchState, PageableData pageableData,
                                                                  DistSearchType searchType, String code, boolean generateASN, final String log,
                                                                  final Map<String, List<String>> otherSearchParams);

    FactFinderProductSearchPageData<SearchStateData, ITEM> search(boolean tracking, SearchStateData searchState, PageableData pageableData,
                                                                  DistSearchType searchType, String code, boolean generateASN, final boolean idsOnly,
                                                                  boolean disableWebUseAttributeCount, final String log,
                                                                  final Map<String, List<String>> otherSearchParams);

    /**
     * This should be used in case of spartacus which sends a session id over parametres.
     */
    FactFinderProductSearchPageData<SearchStateData, ITEM> search(boolean tracking, SearchStateData searchState, PageableData pageableData,
                                                                  DistSearchType searchType, String code, boolean generateASN, final String log,
                                                                  final Map<String, List<String>> otherSearchParams, String sessionId);

    /**
     * This should be used in case of spartacus which sends a session id over parametres.
     */
    FactFinderProductSearchPageData<SearchStateData, ITEM> search(boolean tracking, SearchStateData searchState, PageableData pageableData,
                                                                  DistSearchType searchType, String code, boolean generateASN, final boolean idsOnly,
                                                                  boolean disableWebUseAttributeCount, final String log,
                                                                  final Map<String, List<String>> otherSearchParams, String sessionId);

    FactFinderProductSearchPageData<SearchStateData, ITEM> search(boolean tracking, SearchStateData searchState, PageableData pageableData,
                                                                  DistSearchType searchType, String code, boolean generateASN, final boolean idsOnly,
                                                                  final boolean useCampaigns, boolean disableWebUseAttributeCount,
                                                                  final String log, final Map<String, List<String>> otherSearchParams);

    FactFinderProductSearchPageData<SearchStateData, ITEM> search(final SearchQueryData searchQuery, final PageableData pageableData, final boolean generateASN,
                                                                  final boolean idsOnly, final boolean disableWebUseAttributeCount, final String log,
                                                                  final Map<String, List<String>> otherParams);

    FactFinderProductSearchPageData<SearchStateData, ITEM> search(final SearchQueryData searchQuery, final PageableData pageableData, final boolean generateASN,
                                                                  final boolean idsOnly, final boolean useCampaigns, final boolean disableWebUseAttributeCount,
                                                                  final String log,
                                                                  final Map<String, List<String>> otherParams);

    /**
     * Executes a FactFinder search and returns a list of {@link FactFinderProductSearchPageData}. in the end
     * {@link #search(SearchStateData, PageableData, DistSearchType, String, boolean, boolean, String)} will be called. <br/>
     * <br/>
     *
     * @param searchState
     *            the search query object
     * @param pageableData
     *            the page to return
     * @param searchType
     *            defines the search type
     * @param code
     *            additional code for special searches (e.g. categoryCode, manufacturerCode,...)
     * @param generateASN
     *            whether the search response should contain "After Search Navigation" information
     * @param log
     *            to which log file should this request be logged. If empty Fact Finder will decide which is the right log file.
     * @param disableWebUseAttributeCount
     *            flag to disable the attribute count
     * @param otherSearchParams
     *            other search parameter that get added to the search parameters. If the parameter is not known by FF it get returned as it
     *            is.
     * @return a product list from FactFinder
     */
    FactFinderProductSearchPageData<SearchStateData, ITEM> search(boolean tracking, final SearchStateData searchState, final PageableData pageableData,
                                                                  final DistSearchType searchType, final String code, boolean generateASN,
                                                                  boolean disableWebUseAttributeCount, final String log,
                                                                  final Map<String, List<String>> otherSearchParams);

    /**
     * This should be used in case of spartacus which sends a session id over parametres.
     */
    FactFinderProductSearchPageData<SearchStateData, ITEM> search(boolean tracking, final SearchStateData searchState, final PageableData pageableData,
                                                                  final DistSearchType searchType, final String code, boolean generateASN,
                                                                  boolean disableWebUseAttributeCount, final String log,
                                                                  final Map<String, List<String>> otherSearchParams, String sessionId);

    /**
     *
     * @param searchQuery
     *            search query
     * @param pageableData
     *            the page to return
     * @param generateASN
     *            whether the search response should contain "After Search Navigation" information
     * @param disableWebUseAttributeCount
     *            flag to disable the attribute count
     * @param log
     *            to which log file should this request be logged. If empty Fact Finder will decide which is the right log file.
     * @param otherSearchParams
     *            other search parameter that get added to the search parameters. If the parameter is not known by FF it get returned as it
     *            is.
     * @return a product list from FactFinder
     */
    FactFinderProductSearchPageData<SearchStateData, ITEM> search(final SearchQueryData searchQuery, final PageableData pageableData, boolean generateASN,
                                                                  boolean disableWebUseAttributeCount, final String log,
                                                                  final Map<String, List<String>> otherSearchParams);

    /**
     * Get the auto complete suggestions for the provided input.
     *
     * @param input
     *            the user's input
     * @return a list of suggested search terms
     */
    AutocompleteSuggestion getAutocompleteSuggestions(String input, ArrayOfFilter arrayOfFilter);

    /**
     * Get the auto complete suggestions for direct orders. Return suggestions for product number and type name only.
     *
     * @param input
     *            the user's input
     * @return a list of suggested search terms
     */
    AutocompleteSuggestion getDirectOrderAutocompleteSuggestions(String input, ArrayOfFilter arrayOfFilter);

    /**
     * Get the pagination data (next/prev link) by initiating a search.
     *
     * @param searchState
     *            for what to build the pagination
     * @param pageableData
     *            how the pagination should be built
     * @param searchType
     *            defines the search type
     * @param code
     *            additional code for special searches (e.g. categoryCode, manufacturerCode,...)
     *
     * @return pagination data
     */
    FactFinderPaginationData getPagination(boolean tracking, SearchStateData searchState, PageableData pageableData, DistSearchType searchType, String code);

    /**
     * Load additional facets.
     *
     * @param searchState
     *            the current search state
     * @param pageableData
     *            pagination information
     * @param additionalFacetCode
     *            the code of the additional facet
     * @param searchType
     *            defines the search type
     * @param code
     *            additional code for special searches (e.g. categoryCode, manufacturerCode,...)
     * @return the additionally loaded facet.
     */
    FactFinderFacetData<SearchStateData> loadAdditionalFacet(final SearchStateData searchState, final PageableData pageableData,
                                                             final String additionalFacetCode, DistSearchType searchType, final String code);

    /**
     * creates a {@link SearchQueryData} from a state, searchType and an optional code.
     *
     * @param searchState
     *            current search state
     * @param searchType
     *            search type
     * @param code
     *            code (only needed for some types, e.g. manufacturer code or category code)
     * @return a SearchQueryData
     */
    SearchQueryData decodeState(boolean tracking, final SearchStateData searchState, final DistSearchType searchType, final String code);

    /**
     * This should be used in case of spartacus which sends a session id over parametres.
     */
    SearchQueryData decodeState(boolean tracking, final SearchStateData searchState, final DistSearchType searchType, final String code,
                                String sessionId);

    /**
     * adds the utf-8 url encoded filterstrings to the search page data result.
     *
     * @param searchPageData
     *            current search page data
     */
    void addFilterstrings(final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData);

}

package com.namics.hybris.ffsearch.populator.response;

import java.util.Map;
import java.util.stream.Collectors;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchResponse;

import de.factfinder.webservice.ws71.FFsearch.Result;
import de.factfinder.webservice.ws71.FFsearch.SearchRecord;
import de.factfinder.webservice.ws71.FFsearch.String2StringMap;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;

import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.ORIG_PAGE_SIZE;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.POSITION;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.SIMILARITY;
import static java.util.Collections.emptyList;

public class ResponseResultValuesPopulator<STATE> implements Populator<SearchResponse, FactFinderFacetSearchPageData<STATE, SearchResultValueData>> {

    @Override
    public void populate(final SearchResponse response, final FactFinderFacetSearchPageData<STATE, SearchResultValueData> target) {
        Result searchResult = response.getSearchResult();
        target.setFiltersRemovedGeneralSearch(response.isFiltersRemovedGeneralSearch());
        if (searchResult.getRecords() != null) {
            target.setResults(searchResult.getRecords().getSearchRecord().stream()
                                          .map(searchRecord -> getSearchResultValue(searchRecord, response, target))
                                          .collect(Collectors.toList()));
        } else {
            target.setResults(emptyList());
        }
    }

    private SearchResultValueData getSearchResultValue(SearchRecord source,
                                                       SearchResponse response,
                                                       FactFinderFacetSearchPageData<STATE, SearchResultValueData> target) {
        Map<String, Object> values = source.getRecord().getEntry().stream()
                                                 .collect(Collectors.toMap(String2StringMap.Entry::getKey, String2StringMap.Entry::getValue));
        values.put(POSITION, source.getPosition());
        values.put(SIMILARITY, source.getSearchSimilarity());
        if (response.getSearchResult().getPaging() != null && response.getSearchResult().getPaging().getPagingConf() != null) {
            values.put(ORIG_PAGE_SIZE, response.getSearchResult().getPaging().getPagingConf().getDefaultResultsPerPage());
        }
        final Object mpn = values.get(DistConstants.FactFinder.FF_MPN_ALTERNATIVE_MATCH_KEY);
        if (mpn != null && Boolean.TRUE.equals(Boolean.valueOf(mpn.toString()))) {
            target.setMpnMatch(true);
        }
        SearchResultValueData searchResultValueData = new SearchResultValueData();
        searchResultValueData.setValues(values);
        return searchResultValueData;
    }
}

/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.hybris.ffsearch.populator.response;

import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.POSITION;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.SIMILARITY;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.facet.SingleWordSearchItem;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchResponse;

import de.factfinder.webservice.ws71.FFsearch.SearchRecord;
import de.factfinder.webservice.ws71.FFsearch.SingleWordSearchResult;
import de.factfinder.webservice.ws71.FFsearch.String2StringMap;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * {@code SingleWordSearchItemsPopulator}
 * <p>
 * Populate the {@link FactFinderProductSearchPageData} with the single word search results if they exists.
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.7
 */
public class SingleWordSearchItemsPopulator implements Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> {

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final SearchResponse source, final FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> target)
            throws ConversionException {
        if (source == null || source.getSearchResult() == null || source.getSearchResult().getSingleWordResults() == null
                || source.getSearchResult().getSingleWordResults().getSingleWordSearchResult() == null) {
            return;
        }

        final List<SingleWordSearchResult> singleWordSearchResults = source.getSearchResult().getSingleWordResults().getSingleWordSearchResult();
        target.setSingleWordSearchItems(
                singleWordSearchResults.stream().
                        map(swsr -> toSingleWordSearchItem(swsr)).
                        collect(Collectors.toList()));
    }

    /**
     * Convert an instance of {@link SingleWordSearchResult} to an instance of {@link SingleWordSearchItem}
     * 
     * @param singleWordSearchResult
     *            the source {@link SingleWordSearchResult}
     * @return an instance of {@link SingleWordSearchItem}
     */
    private SingleWordSearchItem<SearchResultValueData> toSingleWordSearchItem(final SingleWordSearchResult singleWordSearchResult) {
        final SingleWordSearchItem<SearchResultValueData> item = new SingleWordSearchItem();
        item.setCount(singleWordSearchResult.getRecordCount().intValue());
        item.setSingleTerm(singleWordSearchResult.getWord());
        if (singleWordSearchResult.getPreviewRecords() != null && singleWordSearchResult.getPreviewRecords().getSearchRecord() != null) {
            item.setItems(singleWordSearchResult.getPreviewRecords().getSearchRecord().stream().map(searchRecord -> convert(searchRecord))
                    .collect(Collectors.toList()));
        }

        return item;
    }

    /**
     * Convert the #SearchRecord to #SearchResultValueData
     * 
     * @param source
     * @return an object of type #SearchResultValueData based on the attributes from #SearchRecord
     */
    private SearchResultValueData convert(final SearchRecord source) {
        final SearchResultValueData prototype = new SearchResultValueData();
        final Map<String, Object> values = source.getRecord().getEntry().stream()
                .collect(Collectors.toMap(String2StringMap.Entry::getKey, String2StringMap.Entry::getValue));
        values.put(POSITION, source.getPosition());
        values.put(SIMILARITY, source.getSearchSimilarity());

        prototype.setValues(values);
        return prototype;
    }
}

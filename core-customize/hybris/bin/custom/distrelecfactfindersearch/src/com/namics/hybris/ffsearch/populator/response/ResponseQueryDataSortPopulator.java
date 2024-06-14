/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.response;

import org.apache.commons.lang.StringUtils;

import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchResponse;
import com.namics.hybris.ffsearch.populator.common.SortCodeTranslator;

import de.factfinder.webservice.ws71.FFsearch.SortItem;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populator for {@link SearchQueryData} sorting value. </br></br> The sort value will be like <code>Title:desc</code> or the empty string
 * "", if no sorting value is set or "relevance" is the current sorting.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class ResponseQueryDataSortPopulator implements Populator<SearchResponse, SearchQueryData> {

    @Override
    public void populate(final SearchResponse source, final SearchQueryData target) throws ConversionException {
        if ((source == null || source.getSearchResult() == null || source.getSearchResult().getSortsList() == null)
                || source.getSearchResult().getSortsList().getSortItem().isEmpty()) {
            // default sorting is "relevance" if none of the items is selected.
            target.setSort(StringUtils.EMPTY);
            return;
        }
        for (final SortItem sortItem : source.getSearchResult().getSortsList().getSortItem()) {
            // fetch the first selected sort item (according to the current specification, there should only be one).
            if (needToAppendSortParam(sortItem, source)) {
                final StringBuilder sortQueryText = new StringBuilder();
                sortQueryText.append(sortItem.getName()).append(":").append(sortItem.getOrder().value());
                target.setSort(sortQueryText.toString());
                return;
            }
        }
    }

    private boolean needToAppendSortParam(final SortItem sortItem, final SearchResponse source) {
        return !sortItem.isRelevanceSortItem().booleanValue() && SortCodeTranslator.isSelectedItem(sortItem, source.getSearchRequest());
    }

}

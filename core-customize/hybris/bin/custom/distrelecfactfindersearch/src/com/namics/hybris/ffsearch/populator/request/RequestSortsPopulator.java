/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.request;

import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.UNDEFINED;

import de.hybris.platform.converters.Populator;
import org.apache.commons.lang.StringUtils;

import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;
import com.namics.hybris.ffsearch.populator.common.SortCodeTranslator;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfSortItem;
import de.factfinder.webservice.ws71.FFsearch.SortItem;
import de.factfinder.webservice.ws71.FFsearch.SortMethods;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;

/**
 * Populator which builds the sorting information for the data object representing a search request.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class RequestSortsPopulator implements Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> {

    @Override
    public void populate(final SearchQueryPageableData<SearchQueryData> source, final SearchRequest target) {
        if (isValidSortValue(source)) {
            buildSortItem(source, target);
        }
    }

    private void buildSortItem(final SearchQueryPageableData<SearchQueryData> source, final SearchRequest searchRequest) {
        // sort direction is supposed to always be passed on in sorting string e.g. "Title:desc".
        final String[] sorting = StringUtils.split(source.getPageableData().getSort(), ':');
        if (sorting.length > 1) {
            final ArrayOfSortItem sortItems = new ArrayOfSortItem();
            final SortItem item = buildSortItem(sorting, searchRequest);
            sortItems.getSortItem().add(item);
            searchRequest.getSearchParams().setSortsList(sortItems);
        }
    }

    /**
     * Only one sorting parameter is currently used.
     */
    private SortItem buildSortItem(final String[] sorting, final SearchRequest searchRequest) {
        final SortItem sortItem = new SortItem();
        sortItem.setName(SortCodeTranslator.getSortCode(sorting[0], searchRequest));
        sortItem.setOrder(SortMethods.fromValue(sorting[1]));
        sortItem.setSelected(Boolean.TRUE);
        return sortItem;
    }

    private boolean isValidSortValue(final SearchQueryPageableData<SearchQueryData> source) {
        return source.getPageableData() != null && StringUtils.isNotBlank(source.getPageableData().getSort())
                && !StringUtils.contains(source.getPageableData().getSort(), UNDEFINED);
    }
}

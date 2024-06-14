/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.request;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchRequest;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.converters.Populator;

/**
 * Populator setting special parameters to optimize Manufacturer search requests.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class RequestManufacturerPopulator implements Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> {

    // String tokens are separated to be able to look for any occurrences of "*"
    private static final String SEARCH_QUERY_SUFFIX = " " + "*";

    @Autowired
    private DistManufacturerService distManufacturerService;

    @Override
    public void populate(final SearchQueryPageableData<SearchQueryData> source, final SearchRequest target) {
        if (DistSearchType.MANUFACTURER.equals(source.getSearchQueryData().getSearchType())) {
            final DistManufacturerModel manufacturer = distManufacturerService.getManufacturerByCode(source.getSearchQueryData().getCode());
            populateQueryString(manufacturer, target);
            populateSearchField(target);
        }
    }

    private void populateQueryString(final DistManufacturerModel manufacturer, final SearchRequest target) {
        target.getSearchParams().setQuery(manufacturer.getName() + SEARCH_QUERY_SUFFIX);
    }

    private void populateSearchField(final SearchRequest target) {
        target.getSearchParams().setSearchField(DistFactFinderExportColumns.MANUFACTURER.getValue());
    }

}

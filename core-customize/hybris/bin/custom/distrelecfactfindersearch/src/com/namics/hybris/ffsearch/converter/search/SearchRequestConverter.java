/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.search;

import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

/**
 * Populating Converter for building up a {@link SearchRequest} from a {@link SearchQueryPageableData}. <br />
 * Populators are listed explicitly in the code for the sake of clarity.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class SearchRequestConverter extends AbstractPopulatingConverter<SearchQueryPageableData<SearchQueryData>, SearchRequest> {

    private Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> requestAdditionalControlParamsPopulator;
    private Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> requestAdditionalSearchParamsPopulator;
    private Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> requestQueryPopulator;
    private Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> requestFiltersPopulator;
    private Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> requestSortsPopulator;
    private Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> requestPaginationPopulator;
    private Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> requestManufacturerPopulator;
    private Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> requestSessionIdPopulator;

    @Override
    public void populate(final SearchQueryPageableData<SearchQueryData> source, final SearchRequest target) {
        getRequestQueryPopulator().populate(source, target);
        getRequestFiltersPopulator().populate(source, target);
        getRequestSortsPopulator().populate(source, target);
        getRequestPaginationPopulator().populate(source, target);
        getRequestManufacturerPopulator().populate(source, target);
        getRequestAdditionalControlParamsPopulator().populate(source, target);
        getRequestAdditionalSearchParamsPopulator().populate(source, target);
        getRequestSessionIdPopulator().populate(source, target);
    }

    @Override
    protected SearchRequest createTarget() {
        return new SearchRequest();
    }

    // BEGIN GENERATED CODE

    public Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> getRequestAdditionalControlParamsPopulator() {
        return requestAdditionalControlParamsPopulator;
    }

    @Required
    public void setRequestAdditionalControlParamsPopulator(
            final Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> requestAdditionalControlParamsPopulator) {
        this.requestAdditionalControlParamsPopulator = requestAdditionalControlParamsPopulator;
    }

    public Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> getRequestAdditionalSearchParamsPopulator() {
        return requestAdditionalSearchParamsPopulator;
    }

    @Required
    public void setRequestAdditionalSearchParamsPopulator(
            final Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> requestAdditionalSearchParamsPopulator) {
        this.requestAdditionalSearchParamsPopulator = requestAdditionalSearchParamsPopulator;
    }

    protected Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> getRequestQueryPopulator() {
        return requestQueryPopulator;
    }

    @Required
    public void setRequestQueryPopulator(final Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> requestQueryPopulator) {
        this.requestQueryPopulator = requestQueryPopulator;
    }

    protected Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> getRequestFiltersPopulator() {
        return requestFiltersPopulator;
    }

    @Required
    public void setRequestFiltersPopulator(final Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> requestFiltersPopulator) {
        this.requestFiltersPopulator = requestFiltersPopulator;
    }

    protected Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> getRequestSortsPopulator() {
        return requestSortsPopulator;
    }

    @Required
    public void setRequestSortsPopulator(final Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> requestSortsPopulator) {
        this.requestSortsPopulator = requestSortsPopulator;
    }

    protected Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> getRequestPaginationPopulator() {
        return requestPaginationPopulator;
    }

    @Required
    public void setRequestPaginationPopulator(final Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> requestPaginationPopulator) {
        this.requestPaginationPopulator = requestPaginationPopulator;
    }

    public Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> getRequestManufacturerPopulator() {
        return requestManufacturerPopulator;
    }

    @Required
    public void setRequestManufacturerPopulator(final Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> requestManufacturerPopulator) {
        this.requestManufacturerPopulator = requestManufacturerPopulator;
    }

    public Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> getRequestSessionIdPopulator() {
        return requestSessionIdPopulator;
    }

    public void setRequestSessionIdPopulator(final Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> requestSessionIdPopulator) {
        this.requestSessionIdPopulator = requestSessionIdPopulator;
    }
}

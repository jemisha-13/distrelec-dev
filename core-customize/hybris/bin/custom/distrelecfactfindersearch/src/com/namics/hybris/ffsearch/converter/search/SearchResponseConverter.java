/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.search;

import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchResponse;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

/**
 * Populating Converter for building up a {@link FactFinderProductSearchPageData} from a {@link SearchResponse}.<br />
 * Populators are listed explicitly in the code for the sake of clarity.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * @param <ITEM>
 */
public class SearchResponseConverter<ITEM> extends AbstractPopulatingConverter<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> {

    private Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseQueryPopulator;
    private Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseResultsPopulator;
    private Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responsePaginationPopulator;
    private Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseSortPopulator;
    private Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseOtherFacetPopulator;
    private Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseLazyFacetPopulator;
    private Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseCategoryFacetPopulator;
    private Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseFilterBadgesPopulator;
    private Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseCampaignPopulator;
    private Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> singleWordSearchItemsPopulator;
    private Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseSessionIdPopulator;

    @Override
    public void populate(final SearchResponse source, final FactFinderProductSearchPageData<SearchQueryData, ITEM> target) {
        getResponseQueryPopulator().populate(source, target);
        getResponseResultsPopulator().populate(source, target);
        getResponsePaginationPopulator().populate(source, target);
        getResponseSortPopulator().populate(source, target);
        getResponseOtherFacetPopulator().populate(source, target);
        getResponseLazyFacetPopulator().populate(source, target);
        getResponseCategoryFacetPopulator().populate(source, target);
        getResponseFilterBadgesPopulator().populate(source, target);
        getResponseCampaignPopulator().populate(source, target);
        getSingleWordSearchItemsPopulator().populate(source, target);
        getResponseSessionIdPopulator().populate(source, target);
    }

    @Override
    protected FactFinderProductSearchPageData<SearchQueryData, ITEM> createTarget() {
        return new FactFinderProductSearchPageData<SearchQueryData, ITEM>();
    }

    // BEGIN GENERATED CODE

    protected Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> getResponseQueryPopulator() {
        return responseQueryPopulator;
    }

    @Required
    public void setResponseQueryPopulator(final Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseQueryPopulator) {
        this.responseQueryPopulator = responseQueryPopulator;
    }

    protected Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> getResponseResultsPopulator() {
        return responseResultsPopulator;
    }

    @Required
    public void setResponseResultsPopulator(final Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseResultsPopulator) {
        this.responseResultsPopulator = responseResultsPopulator;
    }

    protected Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> getResponsePaginationPopulator() {
        return responsePaginationPopulator;
    }

    @Required
    public void setResponsePaginationPopulator(
            final Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responsePaginationPopulator) {
        this.responsePaginationPopulator = responsePaginationPopulator;
    }

    protected Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> getResponseSortPopulator() {
        return responseSortPopulator;
    }

    @Required
    public void setResponseSortPopulator(final Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseSortPopulator) {
        this.responseSortPopulator = responseSortPopulator;
    }

    protected Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> getResponseOtherFacetPopulator() {
        return responseOtherFacetPopulator;
    }

    @Required
    public void setResponseOtherFacetPopulator(
            final Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseOtherFacetPopulator) {
        this.responseOtherFacetPopulator = responseOtherFacetPopulator;
    }

    protected Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> getResponseCategoryFacetPopulator() {
        return responseCategoryFacetPopulator;
    }

    @Required
    public void setResponseCategoryFacetPopulator(
            final Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseCategoryFacetPopulator) {
        this.responseCategoryFacetPopulator = responseCategoryFacetPopulator;
    }

    protected Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> getResponseFilterBadgesPopulator() {
        return responseFilterBadgesPopulator;
    }

    @Required
    public void setResponseFilterBadgesPopulator(
            final Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseFilterBadgesPopulator) {
        this.responseFilterBadgesPopulator = responseFilterBadgesPopulator;
    }

    public Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> getResponseCampaignPopulator() {
        return responseCampaignPopulator;
    }

    @Required
    public void setResponseCampaignPopulator(
            final Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseCampaignPopulator) {
        this.responseCampaignPopulator = responseCampaignPopulator;
    }

    public Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> getResponseLazyFacetPopulator() {
        return responseLazyFacetPopulator;
    }

    @Required
    public void setResponseLazyFacetPopulator(
            final Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseLazyFacetPopulator) {
        this.responseLazyFacetPopulator = responseLazyFacetPopulator;
    }

    public Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> getSingleWordSearchItemsPopulator() {
        return singleWordSearchItemsPopulator;
    }

    public void setSingleWordSearchItemsPopulator(
            Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> singleWordSearchItemsPopulator) {
        this.singleWordSearchItemsPopulator = singleWordSearchItemsPopulator;
    }

    public Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> getResponseSessionIdPopulator() {
        return responseSessionIdPopulator;
    }

    public void setResponseSessionIdPopulator(Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, ITEM>> responseSessionIdPopulator) {
        this.responseSessionIdPopulator = responseSessionIdPopulator;
    }
}

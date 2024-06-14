/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.request;

import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.factfinder.webservice.ws71.FFsearch.SearchControlParams;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;

/**
 * Populator which builds the initial search request for the data object representing a search request. </br>
 * </br>
 * For search requests it is supposed that: </br>
 * </br>
 * - the ASN (After-Search-Navigation) should always be generated. </br>
 * - the Campaigns configured in the FactFinder instance should always be considered. </br>
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class RequestAdditionalControlParamsPopulator implements Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> {

    @Override
    public void populate(final SearchQueryPageableData<SearchQueryData> source, final SearchRequest target) {
        initializeTarget(target);
        populateAdditionalControlParams(source, target);
    }

    private void initializeTarget(final SearchRequest target) {
        if (target.getControlParams() == null) {
            target.setControlParams(new SearchControlParams());
        }
    }

    private void populateAdditionalControlParams(final SearchQueryPageableData<SearchQueryData> source, final SearchRequest target) {
        target.getControlParams().setUseAsn(Boolean.valueOf(source.getSearchQueryData().getAdditionalControlParams().isGenerateASN()));
        target.getControlParams().setUseCampaigns(Boolean.valueOf(source.getSearchQueryData().getAdditionalControlParams().isUseCampaigns()));
        target.getControlParams().setIdsOnly(Boolean.valueOf(source.getSearchQueryData().getAdditionalControlParams().isIdsOnly()));
        target.getControlParams().setGenerateAdvisorTree(Boolean.valueOf(source.getSearchQueryData().getAdditionalControlParams().isGenerateAdvisorTree()));
        target.getControlParams().setUseFoundWords(Boolean.valueOf(source.getSearchQueryData().getAdditionalControlParams().isUseFoundWords()));
        target.getControlParams().setUseKeywords(Boolean.valueOf(source.getSearchQueryData().getAdditionalControlParams().isUseKeywords()));
        target.getControlParams().setUsePersonalization(Boolean.valueOf(source.getSearchQueryData().getAdditionalControlParams().isUsePersonalization()));
    }

}

/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.response;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchResponse;

import de.factfinder.webservice.ws71.FFsearch.Campaign;
import de.factfinder.webservice.ws71.FFsearch.CampaignFlavour;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;

/**
 * Populator for campaign related properties.
 * 
 * @author ceberle, Namics AG
 * @author rhusi, Distrelec
 * @since Distrelec 1.0
 */
public class ResponseCampaignPopulator implements Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> {

    private Populator<List<Campaign>, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> advisorCampaignPopulator;
    private Populator<List<Campaign>, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> feedbackCampaignPopulator;
    private Populator<List<Campaign>, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> redirectCampaignPopulator;

    @Override
    public void populate(final SearchResponse source, final FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> target) {
        if (source == null || source.getSearchResult() == null || source.getSearchResult().getCampaigns() == null
                || source.getSearchResult().getCampaigns().getCampaign() == null) {

            return;
        }

        final List<Campaign> campaigns = source.getSearchResult().getCampaigns().getCampaign();
        getAdvisorCampaignPopulator().populate(filterCampaignsByType(campaigns, CampaignFlavour.ADVISOR), target);
        getFeedbackCampaignPopulator().populate(filterCampaignsByType(campaigns, CampaignFlavour.FEEDBACK), target);
        getRedirectCampaignPopulator().populate(filterCampaignsByType(campaigns, CampaignFlavour.REDIRECT), target);
    }

    protected List<Campaign> filterCampaignsByType(final List<Campaign> campaigns, final CampaignFlavour campaignFlavour) {
        final CampaignFlavour flavour = campaignFlavour;
        final List<Campaign> filteredCampaigns = new ArrayList<Campaign>(campaigns);
        CollectionUtils.filter(filteredCampaigns, new Predicate() {

            @Override
            public boolean evaluate(final Object object) {
                final Campaign campaign = (Campaign) object;
                return flavour.equals(campaign.getFlavour());
            }

        });
        return filteredCampaigns;
    }

    public Populator<List<Campaign>, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> getAdvisorCampaignPopulator() {
        return advisorCampaignPopulator;
    }

    @Required
    public void setAdvisorCampaignPopulator(
            final Populator<List<Campaign>, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> advisorCampaignPopulator) {
        this.advisorCampaignPopulator = advisorCampaignPopulator;
    }

    public Populator<List<Campaign>, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> getFeedbackCampaignPopulator() {
        return feedbackCampaignPopulator;
    }

    @Required
    public void setFeedbackCampaignPopulator(
            final Populator<List<Campaign>, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> feedbackCampaignPopulator) {
        this.feedbackCampaignPopulator = feedbackCampaignPopulator;
    }

    public Populator<List<Campaign>, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> getRedirectCampaignPopulator() {
        return redirectCampaignPopulator;
    }

    @Required
    public void setRedirectCampaignPopulator(
            final Populator<List<Campaign>, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> redirectCampaignPopulator) {
        this.redirectCampaignPopulator = redirectCampaignPopulator;
    }

}

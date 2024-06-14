/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.response;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.factfinder.webservice.ws71.FFsearch.Campaign;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;

/**
 * Populator for Redirect Campaigns.
 * 
 * @author rhusi, Distrelec
 * @since Distrelec 3.0
 */
public class ResponseRedirectCampaignPopulator implements Populator<List<Campaign>, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> {

    @Override
    public void populate(final List<Campaign> source, final FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> target) {
        for (final Campaign campaign : source) {
            if (campaign != null && campaign.getTarget() != null && StringUtils.isNotBlank(campaign.getTarget().getDestination())) {
                target.setKeywordRedirectUrl(campaign.getTarget().getDestination());
                break;
            }
        }
    }

}

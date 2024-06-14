/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.namics.hybris.ffsearch.data.campaign.feedback.FeedbackCampaignData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.factfinder.webservice.ws71.FFsearch.Campaign;
import de.factfinder.webservice.ws71.FFsearch.FeedbackText;
import de.factfinder.webservice.ws71.FFsearch.Record;
import de.factfinder.webservice.ws71.FFsearch.String2StringMap;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;

/**
 * Populator for Feedback Campaigns.
 * 
 * @author rhusi, Distrelec
 * @since Distrelec 3.0
 */
public class ResponseFeedbackCampaignPopulator implements Populator<List<Campaign>, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> {

    @Override
    public void populate(final List<Campaign> source, final FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> target) {
        final List<FeedbackCampaignData<SearchResultValueData>> feedbackCampaigns = new ArrayList<FeedbackCampaignData<SearchResultValueData>>();

        for (final Campaign campaign : source) {
            final FeedbackCampaignData feedbackCampaign = createFeedbackCampaignData(campaign);
            if (feedbackCampaign != null) {
                feedbackCampaigns.add(feedbackCampaign);
            }
        }
        target.setFeedbackCampaigns(feedbackCampaigns);
    }

    protected FeedbackCampaignData createFeedbackCampaignData(final Campaign campaign) {
        if (campaign == null || campaign.getFlavour() == null) {
            return null;
        }

        final FeedbackCampaignData<SearchResultValueData> feedbackCampaign = new FeedbackCampaignData();
        feedbackCampaign.setId(campaign.getId());
        feedbackCampaign.setCategory(campaign.getCategory());
        feedbackCampaign.setName(campaign.getName());
        feedbackCampaign.setFeedbackTexts(getFeedbackTexts(campaign));
        feedbackCampaign.setPushedProducts(getPushedProducts(campaign));

        if (MapUtils.isEmpty(feedbackCampaign.getFeedbackTexts()) && CollectionUtils.isEmpty(feedbackCampaign.getPushedProducts())) {
            return null;
        } else {
            return feedbackCampaign;
        }
    }

    protected Map<String, String> getFeedbackTexts(final Campaign campaign) {
        final Map<String, String> feedbackTexts = new HashMap<String, String>();

        if (campaign.getFeedbackTexts() != null && CollectionUtils.isNotEmpty(campaign.getFeedbackTexts().getFeedbackText())) {
            for (final FeedbackText feedbackText : campaign.getFeedbackTexts().getFeedbackText()) {
                if (StringUtils.isNotBlank(feedbackText.getLabel()) && StringUtils.isNotBlank(feedbackText.getText())) {
                    feedbackTexts.put(feedbackText.getLabel(), feedbackText.getText());
                }
            }
        }

        return feedbackTexts;
    }

    protected List<SearchResultValueData> getPushedProducts(final Campaign campaign) {
        final List<SearchResultValueData> searchResultValues = new ArrayList<SearchResultValueData>();

        if (campaign.getPushedProductsRecords() != null && CollectionUtils.isNotEmpty(campaign.getPushedProductsRecords().getRecord())) {
            for (final Record record : campaign.getPushedProductsRecords().getRecord()) {
                if (record == null || record.getRecord() == null) {
                    continue;
                }

                final SearchResultValueData searchResultValue = new SearchResultValueData();
                final Map<String, Object> values = new HashMap<String, Object>();

                for (final String2StringMap.Entry entry : record.getRecord().getEntry()) {
                    if (entry != null) {
                        values.put(entry.getKey(), entry.getValue());
                    }
                }
                searchResultValue.setValues(values);
                searchResultValues.add(searchResultValue);
            }
        }

        return searchResultValues;
    }

}

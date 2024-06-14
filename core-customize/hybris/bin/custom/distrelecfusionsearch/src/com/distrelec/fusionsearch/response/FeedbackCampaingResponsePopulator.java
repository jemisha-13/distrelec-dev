package com.distrelec.fusionsearch.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.distrelec.fusion.search.dto.FusionDTO;
import com.distrelec.fusion.search.dto.SearchResponseDTO;
import com.namics.hybris.ffsearch.data.campaign.feedback.FeedbackCampaignData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

class FeedbackCampaingResponsePopulator implements Populator<SearchResponseTuple, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> {

    private final static String SEARCHRESULT_TOP = "Fusion_SearchResult_top";

    @Override
    public void populate(SearchResponseTuple searchResponseTuple,
                         FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> searchPageData) throws ConversionException {
        SearchResponseDTO searchResponseDTO = searchResponseTuple.getSearchResponseDTO();
        FusionDTO fusionDTO = searchResponseDTO.getFusion();
        Boolean removedCategoryCodeFilter = fusionDTO.getRemovedCategoryCodeFilter();
        if (Boolean.TRUE.equals(removedCategoryCodeFilter)) {
            populateZeroResultsCampaign(searchPageData);
        }
    }

    private void populateZeroResultsCampaign(FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> searchPageData) {

        List<FeedbackCampaignData<SearchResultValueData>> feedbackCampaigns = searchPageData.getFeedbackCampaigns();
        if (feedbackCampaigns == null) {
            feedbackCampaigns = new ArrayList<>();
            searchPageData.setFeedbackCampaigns(feedbackCampaigns);
        }

        FeedbackCampaignData feedbackCampaign = new FeedbackCampaignData();
        feedbackCampaign.setFeedbackTexts(Map.of(SEARCHRESULT_TOP, SEARCHRESULT_TOP));
        feedbackCampaigns.add(feedbackCampaign);
    }
}

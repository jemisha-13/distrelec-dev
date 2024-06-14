/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.campaign;

import java.util.List;

import com.google.common.collect.Lists;
import com.namics.hybris.ffsearch.data.campaign.CampaignResponse;
import com.namics.hybris.ffsearch.data.campaign.FactFinderFeedbackTextData;

import de.factfinder.webservice.ws71.FFcampaign.ArrayOfFeedbackText;
import de.factfinder.webservice.ws71.FFcampaign.Campaign;
import de.factfinder.webservice.ws71.FFcampaign.FeedbackText;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Response converter turning {@link CampaignResponse} into a list of {@link FactFinderFeedbackTextData}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class CampaignResponseFeedbackTextConverter implements Converter<CampaignResponse, List<FactFinderFeedbackTextData>> {

    @Override
    public List<FactFinderFeedbackTextData> convert(final CampaignResponse source, final List<FactFinderFeedbackTextData> target) throws ConversionException {
        for (final Campaign campaign : source.getCampaigns().getCampaign()) {
            final ArrayOfFeedbackText feedbackTexts = campaign.getFeedbackTexts();
            if (feedbackTexts == null) {
                continue;
            }
            for (final FeedbackText text : feedbackTexts.getFeedbackText()) {
                final FactFinderFeedbackTextData textData = convert(text);
                target.add(textData);
            }
        }
        return target;
    }

    private FactFinderFeedbackTextData convert(final FeedbackText source) {
        final FactFinderFeedbackTextData result = new FactFinderFeedbackTextData();
        result.setId(source.getId().intValue());
        result.setHtml(source.isHtml().booleanValue());
        result.setLabel(source.getLabel());
        result.setText(source.getText());
        return result;
    }

    @Override
    public List<FactFinderFeedbackTextData> convert(final CampaignResponse source) throws ConversionException {
        return convert(source, createTarget());
    }

    protected List<FactFinderFeedbackTextData> createTarget() {
        return Lists.newArrayList();
    }

}

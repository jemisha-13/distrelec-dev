/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter;

import com.namics.hybris.ffsearch.data.campaign.feedback.FeedbackCampaignData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections.CollectionUtils;

/**
 * Converts Feedback Campaigns.
 *
 * @author ceberle, Namics AG
 * @since Distrelec 1.1
 *
 * @param <RESULT>
 * @param <ITEM>
 */
public class FeedbackCampaignConverter<RESULT, ITEM> extends AbstractConverter<FeedbackCampaignData<RESULT>, FeedbackCampaignData<ITEM>> {

    private Converter<RESULT, ITEM> searchResultProductConverter;

    @Override
    protected FeedbackCampaignData<ITEM> createTarget() {
        return new FeedbackCampaignData<ITEM>();
    }

    @Override
    public void populate(final FeedbackCampaignData<RESULT> source, final FeedbackCampaignData<ITEM> target) {
        target.setId(source.getId());
        target.setCategory(source.getCategory());
        target.setName(source.getName());
        target.setFeedbackTexts(source.getFeedbackTexts());

        if (CollectionUtils.isNotEmpty(source.getPushedProducts())) {
            target.setPushedProducts(Converters.convertAll(source.getPushedProducts(), getSearchResultProductConverter()));
        }
    }

    public Converter<RESULT, ITEM> getSearchResultProductConverter() {
        return searchResultProductConverter;
    }

    public void setSearchResultProductConverter(final Converter<RESULT, ITEM> searchResultProductConverter) {
        this.searchResultProductConverter = searchResultProductConverter;
    }

}

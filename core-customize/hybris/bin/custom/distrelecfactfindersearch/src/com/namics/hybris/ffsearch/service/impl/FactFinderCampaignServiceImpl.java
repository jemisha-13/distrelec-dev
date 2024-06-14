/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.data.campaign.CampaignRequest;
import com.namics.hybris.ffsearch.data.campaign.CampaignResponse;
import com.namics.hybris.ffsearch.data.campaign.CartCampaignRequest;
import com.namics.hybris.ffsearch.service.FactFinderCampaignService;
import com.namics.hybris.ffsearch.service.FactFinderChannelService;

import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * The FactFinder campaign service implementation of {@link FactFinderCampaignService}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class FactFinderCampaignServiceImpl<RESULT, TEXT> implements FactFinderCampaignService<List<RESULT>, List<TEXT>> {

    private FactFinderChannelService channelService;
    private Converter<CartCampaignRequest, CampaignResponse> cartRequestConverter;
    private Converter<CampaignRequest, CampaignResponse> requestConverter;
    private Converter<CampaignResponse, List<RESULT>> responseConverter;
    private Converter<CampaignResponse, List<TEXT>> responseFeedbackTextConverter;

    @Override
    public List<RESULT> getCartPushedProducts(final List<String> productCodes) {
        final CartCampaignRequest request = new CartCampaignRequest();
        request.setProductCodes(productCodes);
        request.setChannel(getChannelService().getCurrentFactFinderChannel());
        // Execute the request
        final CampaignResponse response = getCartRequestConverter().convert(request);
        // Convert the response
        return getResponseConverter().convert(response);
    }

    @Override
    public List<RESULT> getPushedProducts(final String productCode) {
        final CampaignRequest request = new CampaignRequest();
        request.setProductCode(productCode);
        request.setChannel(getChannelService().getCurrentFactFinderChannel());
        // Execute the request
        final CampaignResponse response = getRequestConverter().convert(request);
        // Convert the response
        return getResponseConverter().convert(response);
    }

    @Override
    public List<TEXT> getFeedbackTexts(final String productCode) {
        final CampaignRequest request = new CampaignRequest();
        request.setProductCode(productCode);
        request.setChannel(getChannelService().getCurrentFactFinderChannel());
        // Execute the request
        final CampaignResponse response = getRequestConverter().convert(request);
        // Convert the response
        return getResponseFeedbackTextConverter().convert(response);
    }

    // BEGIN GENERATED CODE

    protected FactFinderChannelService getChannelService() {
        return channelService;
    }

    @Required
    public void setChannelService(final FactFinderChannelService channelService) {
        this.channelService = channelService;
    }

    protected Converter<CampaignRequest, CampaignResponse> getRequestConverter() {
        return requestConverter;
    }

    @Required
    public void setRequestConverter(final Converter<CampaignRequest, CampaignResponse> requestConverter) {
        this.requestConverter = requestConverter;
    }

    public Converter<CampaignResponse, List<TEXT>> getResponseFeedbackTextConverter() {
        return responseFeedbackTextConverter;
    }

    public void setResponseFeedbackTextConverter(final Converter<CampaignResponse, List<TEXT>> responseFeedbackTextConverter) {
        this.responseFeedbackTextConverter = responseFeedbackTextConverter;
    }

    public Converter<CampaignResponse, List<RESULT>> getResponseConverter() {
        return responseConverter;
    }

    public void setResponseConverter(final Converter<CampaignResponse, List<RESULT>> responseConverter) {
        this.responseConverter = responseConverter;
    }

    public Converter<CartCampaignRequest, CampaignResponse> getCartRequestConverter() {
        return cartRequestConverter;
    }

    public void setCartRequestConverter(final Converter<CartCampaignRequest, CampaignResponse> cartRequestConverter) {
        this.cartRequestConverter = cartRequestConverter;
    }

}

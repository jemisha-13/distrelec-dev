/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.data.recommendation.CartRecommendationRequest;
import com.namics.hybris.ffsearch.data.recommendation.RecommendationRequest;
import com.namics.hybris.ffsearch.data.recommendation.RecommendationResponse;
import com.namics.hybris.ffsearch.service.FactFinderChannelService;
import com.namics.hybris.ffsearch.service.FactFinderRecommendationService;

import de.hybris.platform.impex.jalo.ErrorHandler.RESULT;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * The FactFinder recommendation service implementation of {@link FactFinderRecommendationService}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class FactFinderRecommendationServiceImpl implements FactFinderRecommendationService<List<RESULT>> {

    private FactFinderChannelService channelService;
    private Converter<CartRecommendationRequest, RecommendationResponse> cartRequestConverter;
    private Converter<RecommendationRequest, RecommendationResponse> requestConverter;
    private Converter<RecommendationResponse, List<RESULT>> responseConverter;

    @Override
    public List<RESULT> getCartRecommendedProducts(final List<String> productCodes) {
        final CartRecommendationRequest request = new CartRecommendationRequest();
        request.setProductCodes(productCodes);
        request.setChannel(getChannelService().getCurrentFactFinderChannel());
        // Execute the request
        final RecommendationResponse response = getCartRequestConverter().convert(request);
        // Convert the response
        return getResponseConverter().convert(response);

    }

    @Override
    public List<RESULT> getAlsoBought(final String productCode) {
        final RecommendationRequest request = new RecommendationRequest();
        request.setProductCode(productCode);
        request.setChannel(getChannelService().getCurrentFactFinderChannel());
        // Execute the request
        final RecommendationResponse response = getRequestConverter().convert(request);
        // Convert the response
        return getResponseConverter().convert(response);

    }

    // // BEGIN GENERATED CODE

    protected FactFinderChannelService getChannelService() {
        return channelService;
    }

    @Required
    public void setChannelService(final FactFinderChannelService channelService) {
        this.channelService = channelService;
    }

    protected Converter<RecommendationRequest, RecommendationResponse> getRequestConverter() {
        return requestConverter;
    }

    @Required
    public void setRequestConverter(final Converter<RecommendationRequest, RecommendationResponse> recommendationRequestConverter) {
        this.requestConverter = recommendationRequestConverter;
    }

    protected Converter<RecommendationResponse, List<RESULT>> getResponseConverter() {
        return responseConverter;
    }

    @Required
    public void setResponseConverter(final Converter<RecommendationResponse, List<RESULT>> recommendationResponseConverter) {
        this.responseConverter = recommendationResponseConverter;
    }

    public Converter<CartRecommendationRequest, RecommendationResponse> getCartRequestConverter() {
        return cartRequestConverter;
    }

    public void setCartRequestConverter(final Converter<CartRecommendationRequest, RecommendationResponse> cartRequestConverter) {
        this.cartRequestConverter = cartRequestConverter;
    }

}

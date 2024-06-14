/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.reco.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.facades.reco.DistRecommendationFacade;
import com.namics.hybris.ffsearch.data.campaign.FactFinderFeedbackTextData;
import com.namics.hybris.ffsearch.service.FactFinderCampaignService;
import com.namics.hybris.ffsearch.service.FactFinderRecommendationService;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Default implementation of {@link DistRecommendationFacade}.
 *
 * @param <RESULT>
 *            Type of data to be converted to a ProductData.
 */
public class DefaultDistRecommendationFacade<RESULT> implements DistRecommendationFacade<ProductData, FactFinderFeedbackTextData> {

    public static final String MAIN_ID_PARAMETER_NAME = "mainId";

    private FactFinderCampaignService<List<RESULT>, List<FactFinderFeedbackTextData>> campaignService;

    private FactFinderRecommendationService<List<RESULT>> recommendationService;

    private Converter<RESULT, ProductData> searchResultProductConverter;

    @Override
    public List<ProductData> getCartRecommendedProducts(final List<String> productCodes) {
        final List<ProductData> products = Converters.convertAll(getRecommendationService().getCartRecommendedProducts(productCodes),
                                                                 getSearchResultProductConverter());
        return products;
    }

    @Override
    public List<ProductData> getRecommendedProducts(final String productCode) {
        final List<ProductData> products = Converters.convertAll(getCampaignService().getPushedProducts(productCode), getSearchResultProductConverter());
        appendRecommendationParentId(products, productCode);
        return products;
    }

    @Override
    public List<FactFinderFeedbackTextData> getFeedbackTexts(final String productCode) {
        return getCampaignService().getFeedbackTexts(productCode);
    }

    @Override
    public List<ProductData> getAlsoBoughtProducts(final String productCode) {
        final List<ProductData> products = Converters.convertAll(getRecommendationService().getAlsoBought(productCode), getSearchResultProductConverter())
                                                     .stream()
                                                     .filter(ProductData::isBuyable)
                                                     .collect(Collectors.toList());
        appendRecommendationParentId(products, productCode);
        return products;
    }

    private void appendRecommendationParentId(final List<ProductData> products, final String parentProductCode) {
        for (final ProductData product : products) {
            final StringBuilder url = new StringBuilder(product.getUrl());
            if (StringUtils.contains(product.getUrl(), '?')) {
                url.append('&').append(MAIN_ID_PARAMETER_NAME).append('=').append(parentProductCode);
            } else {
                url.append('?').append(MAIN_ID_PARAMETER_NAME).append('=').append(parentProductCode);
            }
            product.setUrl(url.toString());
        }
    }

    // / BEGIN GENERATED CODE

    protected FactFinderCampaignService<List<RESULT>, List<FactFinderFeedbackTextData>> getCampaignService() {
        return campaignService;
    }

    @Required
    public void setCampaignService(final FactFinderCampaignService<List<RESULT>, List<FactFinderFeedbackTextData>> campaignService) {
        this.campaignService = campaignService;
    }

    protected FactFinderRecommendationService<List<RESULT>> getRecommendationService() {
        return recommendationService;
    }

    @Required
    public void setRecommendationService(final FactFinderRecommendationService<List<RESULT>> recommendationService) {
        this.recommendationService = recommendationService;
    }

    protected Converter<RESULT, ProductData> getSearchResultProductConverter() {
        return searchResultProductConverter;
    }

    @Required
    public void setSearchResultProductConverter(final Converter<RESULT, ProductData> searchResultProductConverter) {
        this.searchResultProductConverter = searchResultProductConverter;
    }

}

/*
 * Copyright 2000-2015 DISTRELEC. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.campaigns.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.facades.campaigns.DistCampaignFacade;
import com.namics.hybris.ffsearch.data.campaign.FactFinderFeedbackTextData;
import com.namics.hybris.ffsearch.service.FactFinderCampaignService;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultDistCampaignFacade<RESULT> implements DistCampaignFacade<ProductData> {

    private static final String MAIN_ID_PARAMETER_NAME = "mainId";

    private FactFinderCampaignService<List<RESULT>, List<FactFinderFeedbackTextData>> campaignService;

    private Converter<RESULT, ProductData> searchResultProductConverter;

    @Override
    public List<ProductData> getCartCampaignProducts(final List<String> productCodes) {
        final List<ProductData> products = Converters.convertAll(getCampaignService().getCartPushedProducts(productCodes),
                                                                 getSearchResultProductConverter());
        return products;
    }

    @Override
    public List<ProductData> getCampaignProducts(final String productCode) {
        final List<ProductData> products = Converters.convertAll(getCampaignService().getPushedProducts(productCode), getSearchResultProductConverter());
        appendRecommendationParentId(products, productCode);
        return products;
    }

    public FactFinderCampaignService<List<RESULT>, List<FactFinderFeedbackTextData>> getCampaignService() {
        return campaignService;
    }

    public void setCampaignService(final FactFinderCampaignService<List<RESULT>, List<FactFinderFeedbackTextData>> campaignService) {
        this.campaignService = campaignService;
    }

    public Converter<RESULT, ProductData> getSearchResultProductConverter() {
        return searchResultProductConverter;
    }

    @Required
    public void setSearchResultProductConverter(final Converter<RESULT, ProductData> searchResultProductConverter) {
        this.searchResultProductConverter = searchResultProductConverter;
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

}

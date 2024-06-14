/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.facades.reco.DistRecommendationFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.hybris.ffsearch.data.campaign.FactFinderFeedbackTextData;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Controller to handle AJAX requests for "recommended" and "also bought" products.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
@Controller
@RequestMapping(value = ProductPageController.PRODUCT_PAGE_REQUEST_MAPPING)
public class ProductPageRecommendationController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductPageRecommendationController.class);

    public static final String RECOMMENDATION_PATH = "/recommendation";

    public static final String ALSO_BOUGHT_PATH = "/alsoBought";

    public static final String FEEDBACK_CAMPAINS_PATH = "/feedback-campaigns";

    public static final String PRODUCT_PAGE_RECOMMENDATION_REQUEST_MAPPING = ProductPageController.PRODUCT_CODE_PATH_VARIABLE_PATTERN + RECOMMENDATION_PATH;

    public static final String PRODUCT_PAGE_ALSO_BOUGHT_REQUEST_MAPPING = ProductPageController.PRODUCT_CODE_PATH_VARIABLE_PATTERN + ALSO_BOUGHT_PATH;

    public static final String PRODUCT_FEEDBACK_CAMPAINS_REQUEST_MAPPING = ProductPageController.PRODUCT_CODE_PATH_VARIABLE_PATTERN + FEEDBACK_CAMPAINS_PATH;

    private static final String CAROUSEL_DATA_ATTRIBUTE_NAME = "productFFCarouselData";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    @Qualifier("distRecommendationFacade")
    private DistRecommendationFacade<ProductData, FactFinderFeedbackTextData> recommendationFacade;

    @RequestMapping(value = PRODUCT_PAGE_RECOMMENDATION_REQUEST_MAPPING)
    public String recommendation(@PathVariable("productCode") final String productCode, final Model model) {
        final List<ProductData> carouselData = getRecommendationFacade().getRecommendedProducts(productCode);
        LOG.debug("Fetched recommended products [{}]", ReflectionToStringBuilder.toString(carouselData));
        model.addAttribute(CAROUSEL_DATA_ATTRIBUTE_NAME, carouselData);
        return ControllerConstants.Views.Fragments.Product.CarouselProducts;
    }

    @RequestMapping(value = PRODUCT_PAGE_ALSO_BOUGHT_REQUEST_MAPPING)
    public String alsoBought(@PathVariable("productCode") final String productCode, final Model model) {
        final List<ProductData> carouselData = getRecommendationFacade().getAlsoBoughtProducts(productCode);
        LOG.debug("Fetched alsobought products [{}]", ReflectionToStringBuilder.toString(carouselData));
        model.addAttribute(CAROUSEL_DATA_ATTRIBUTE_NAME, carouselData);
        return ControllerConstants.Views.Fragments.Product.CarouselProducts;
    }

    @RequestMapping(value = PRODUCT_FEEDBACK_CAMPAINS_REQUEST_MAPPING, method = RequestMethod.GET, produces = "application/json")
    public String feedbackCampaigns(@PathVariable("productCode") final String productCode, final Model model) throws CMSItemNotFoundException {
        // check if campaigns for the productdetail page are deactivated
        if (configurationService.getConfiguration().getBoolean(DistConstants.PropKey.Shop.CAMPAIGNS_ON_PRODUCT_DETAIL_PAGE, false)) {
            model.addAttribute("campaigns", Collections.<FactFinderFeedbackTextData> emptyList());
            return ControllerConstants.Views.Fragments.Product.ProductFeedbackCampaigns;
        }
        final List<FactFinderFeedbackTextData> campaigns = recommendationFacade.getFeedbackTexts(productCode);

        if (CollectionUtils.isNotEmpty(campaigns)) {
            java.util.Collections.sort(campaigns, new Comparator<>() {

                @Override
                public int compare(final FactFinderFeedbackTextData param1, final FactFinderFeedbackTextData param2) {
                    return param1.getLabel().compareTo(param2.getLabel());
                }
            });
        }

        model.addAttribute("campaigns", CollectionUtils.isNotEmpty(campaigns) ? campaigns : Collections.<FactFinderFeedbackTextData> emptyList());

        return ControllerConstants.Views.Fragments.Product.ProductFeedbackCampaigns;
    }

    public DistRecommendationFacade<ProductData, FactFinderFeedbackTextData> getRecommendationFacade() {
        return recommendationFacade;
    }

    public void setRecommendationFacade(final DistRecommendationFacade<ProductData, FactFinderFeedbackTextData> recommendationFacade) {
        this.recommendationFacade = recommendationFacade;
    }

}

/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.FactFinderRecommendationType;
import com.namics.distrelec.b2b.core.model.cms2.components.DistProductFFCarouselComponentModel;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.pages.CartPageController;
import com.namics.distrelec.b2b.storefront.controllers.pages.ProductPageRecommendationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for CMS DistProductFFCarouselComponent.
 */
@Controller("DistProductFFCarouselComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistProductFFCarouselComponent)
public class DistProductFFCarouselComponentController extends AbstractDistCMSComponentController<DistProductFFCarouselComponentModel> {

    private static final String AUTOPLAY = "autoplay";
    private static final String CAROUSEL_DATA_PATH = "productFFCarouselDataPath";

    @Autowired
    @Qualifier("webtrekkFacade")
    private DistWebtrekkFacade distWebtrekkFacade;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistProductFFCarouselComponentModel component) {
        if (component.getRecommendationType().equals(FactFinderRecommendationType.RECOMMENDATION)) {
            model.addAttribute(CAROUSEL_DATA_PATH, ProductPageRecommendationController.RECOMMENDATION_PATH);
        } else if (component.getRecommendationType().equals(FactFinderRecommendationType.ALSOBOUGHT)) {
            model.addAttribute(CAROUSEL_DATA_PATH, ProductPageRecommendationController.ALSO_BOUGHT_PATH);
        } else if (component.getRecommendationType().equals(FactFinderRecommendationType.CAMPAIGNPUSHEDPRODUCTS)) {
            model.addAttribute(CAROUSEL_DATA_PATH, CartPageController.PUSHED_CAMPAIGN_PRODUCTS_PATH);
        }

        Boolean autoplay;
        if (component.getAutoplayTimeout() == null) {
            autoplay = Boolean.FALSE;
        } else {
            autoplay = Boolean.TRUE;
        }
        model.addAttribute(AUTOPLAY, autoplay);

        distWebtrekkFacade.addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_FAF_RECO);
    }
}

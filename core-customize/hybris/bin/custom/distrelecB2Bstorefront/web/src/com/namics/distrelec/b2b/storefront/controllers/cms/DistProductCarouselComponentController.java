/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.cms2.components.DistProductCarouselComponentModel;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;

import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * Controller for CMS DistProductCarouselComponent.
 */
@Controller("DistProductCarouselComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistProductCarouselComponent)
public class DistProductCarouselComponentController extends AbstractDistCMSComponentController<DistProductCarouselComponentModel> {
    protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.PROMOTION_LABELS,
                                                                               ProductOption.DIST_MANUFACTURER);

    private static final String AUTOPLAY = "autoplay";

    @Autowired
    private DistrelecProductFacade productFacade;

    @Autowired
    @Qualifier("webtrekkFacade")
    private DistWebtrekkFacade distWebtrekkFacade;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistProductCarouselComponentModel component) {
        final List<ProductData> products = new ArrayList<>();
        for (final ProductModel product : component.getProducts()) {
            ProductData productData = productFacade.getProductForCodeAndOptions(product.getCode(), PRODUCT_OPTIONS);
            if (productData != null) {
                productData.setSalesUnit(productFacade.getRelevantSalesUnit(product.getCode()));
            }
            if (productFacade.isProductBuyable(product.getCode())) {
                products.add(productData);

            }
        }

        if (component.getAutoplayTimeout() == null) {
            model.addAttribute(AUTOPLAY, Boolean.FALSE);
        } else {
            model.addAttribute(AUTOPLAY, Boolean.TRUE);
        }
        model.addAttribute("productCarouselData", products);

        distWebtrekkFacade.addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
    }

    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }
}

/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.cms2.components.DistProductCookieCarouselComponentModel;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.util.CookieUtils;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller for CMS DistProductCookieCarouselComponent.
 */
@Controller("DistProductCookieCarouselComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistProductCookieCarouselComponent)
public class DistProductCookieCarouselComponentController extends AbstractDistCMSComponentController<DistProductCookieCarouselComponentModel> {
    private static final Logger LOG = Logger.getLogger(DistProductCookieCarouselComponentController.class);
    private static final String AUTOPLAY = "autoplay";
    protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.PROMOTION_LABELS,
            ProductOption.DIST_MANUFACTURER);

    @Autowired
    @Qualifier("productFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    @Qualifier("webtrekkFacade")
    private DistWebtrekkFacade distWebtrekkFacade;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistProductCookieCarouselComponentModel component) {
        final String cookieName = component.getCookieName();
        final Cookie cookie = CookieUtils.getCookieForName(request.getCookies(), cookieName);
        if (cookie != null) {
            if (StringUtils.isNotEmpty(cookie.getValue())) {
                final String value = cookie.getValue();
                LOG.debug(value);
                final String[] productCodes = StringUtils.split(value, ":");
                final List<ProductData> products = new ArrayList<ProductData>();

                for (final String code : productCodes) {
                    final ProductData product = getProductDataForCode(code);
                    if (product != null) {
                        products.add(product);
                    }
                }

                if (component.getAutoplayTimeout() == null) {
                    model.addAttribute(AUTOPLAY, Boolean.FALSE);
                } else {
                    model.addAttribute(AUTOPLAY, Boolean.TRUE);
                }
                model.addAttribute("productCookieCarouselData", products);

                distWebtrekkFacade.addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
            }
        }
    }

    protected ProductData getProductDataForCode(final String code) {
        try {
            return productFacade.getProductForCodeAndOptions(code, PRODUCT_OPTIONS);
        } catch (final UnknownIdentifierException e) {
            // catch exception in case the user modified the cookie with some random code.
            LOG.error("Product with code " + code + " from cookie not found.", e);
        }
        return null;
    }

}

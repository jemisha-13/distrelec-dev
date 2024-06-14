/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.storefront.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.security.SameSiteService;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.order.cart.DistCartFacade;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.storesession.data.ShoppingSettingsCookieData;
import com.namics.distrelec.b2b.facades.tracking.DistTrackingFactFinderFacade;
import com.namics.distrelec.b2b.facades.wishlist.DistCompareListFacade;
import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.controllers.util.ShopSettingsUtil;

import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Success provider to handle user settings and ensuring the cart is handled correctly before invoking spring success handler.
 */
public class DistStorefrontAuthenticationSuccessProvider {

    private static final Logger LOG = LogManager.getLogger(DistStorefrontAuthenticationSuccessProvider.class);

    @Autowired
    private DistCustomerFacade b2bCustomerFacade;

    @Autowired
    private DistCartFacade cartFacade;

    @Autowired
    private DistCompareListFacade distCompareListFacade;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Autowired
    private DistTrackingFactFinderFacade trackingFacade;

    @Autowired
    private SameSiteService sameSiteService;

    /**
     * Add the request related attributes to the model.
     *
     * @param request
     * @param response
     * @param authentication
     */
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication)
                                                                                                                                                   throws IOException {

        sessionService.removeAttribute("doNotCalculateTax");

        b2bCustomerFacade.checkAndUpdateCustomer();
        // Check if the customer is active
        if (!checkActive(response, authentication)) {
            return;
        }
        // Update the user data
        b2bCustomerFacade.loginSuccess();
        // Store the user IP address
        b2bCustomerFacade.storeIPAddress();
        cartFacade.persistCart();

        // update shopSettingsCoockie
        updateShopSettingsCookie(request, response);

        final String compareList = Attributes.COMPARE.getValueFromCookies(request);
        if (compareList != null) {
            final String newCompareList = distCompareListFacade.addCookieProductsToCompareList(compareList);
            if (newCompareList != null) {
                Attributes.COMPARE.setValue(request, response, newCompareList);
            } else {
                Attributes.COMPARE.removeValue(response);
            }
        }

        removeAnonymousCheckout();

        // Update newsletter status (asynchronously)

        if (request != null && request.getServletPath().contains("checkout")) {
            request.getSession().setAttribute(WebConstants.CHECKOUT_LOGIN_SUCCESS, Boolean.TRUE);
        } else {
            request.getSession().setAttribute(WebConstants.LOGIN_SUCCESS, Boolean.TRUE);

        }

        // Punchouts
        sessionService.setAttribute(DistConstants.Session.PRODUCT_HIERARCHY_LOADED, Boolean.FALSE);
        sessionService.setAttribute(DistConstants.Session.MAIN_NAV_FLUSH, Boolean.TRUE);
        // SEO Cookies
        request.getSession().setAttribute(DistConstants.Session.LOG_IN_PRESS, Boolean.TRUE.toString());

        // track userlogin in factfinder
        trackingFacade.trackLogin(request, b2bCustomerFacade.getCurrentCustomer());

        if (sameSiteService.is3rdPartyCookieAccessRequired()) {
            sameSiteService.allow3rdPartyCookieAccess(request, response);
        }

        // Update Customer
        String isMarketingCookieEnabled = Attributes.FF_ENSIGTHEN_MARKETING_COOKIE.getValueFromCookies(request);
        b2bCustomerFacade.updateMarketingCookieConsent("1".equals(isMarketingCookieEnabled));

    }

    private boolean checkActive(final HttpServletResponse response, final Authentication authentication) throws IOException {
        if (!b2bCustomerFacade.getCurrentCustomer().isActive()) {
            authentication.setAuthenticated(false);
            response.sendRedirect("/logout?active=false");
            LOG.warn("{} {} Customer [{}] is inactive", ErrorLogCode.INACTIVE_USER_ERROR, ErrorSource.HYBRIS, authentication.getName());
            return false;
        }

        return true;
    }

    protected void updateShopSettingsCookie(final HttpServletRequest request, final HttpServletResponse response) {
        // get settings from cookie if available
        final String shopSettingsString = Attributes.SHOP_SETTINGS.getValueFromCookies(request);
        if (shopSettingsString != null) {
            final ShoppingSettingsCookieData cookieData = ShopSettingsUtil.readShopSettingsCookie(shopSettingsString);
            final String channel = storeSessionFacade.getCurrentChannel().getType();
            String language = storeSessionFacade.getCurrentLanguage().getIsocode();
            String country = storeSessionFacade.getCurrentCountry().getIsocode();
            Boolean cookieMessageConfirmed = Boolean.FALSE;
            Boolean useIconView = storeSessionFacade.isUseIconView();
            Boolean useListView = storeSessionFacade.isUseListView();
            Boolean autoApplyFilter = storeSessionFacade.isAutoApplyFilter();
            Boolean useDetailView = storeSessionFacade.isUseDetailView();
            String itemsPerPage = WebConstants.DEFAULT_ITEMS_PER_PAGE;
            if (cookieData != null) {
                language = cookieData.getLanguage();
                country = cookieData.getCountry();
                cookieMessageConfirmed = cookieData.getCookieMessageConfirmed();
                useIconView = cookieData.getUseIconView();
                useListView = cookieData.getUseListView();
                autoApplyFilter = cookieData.getAutoApplyFilter();
                itemsPerPage = cookieData.getItemsPerPage();
                useDetailView = cookieData.getUseDetailView();
            } else {
                LOG.error("{} {} Cannot parse shop settings cookie! Use current session values instead.", ErrorLogCode.SESSION_COOKIE_ERROR,
                          ErrorSource.HYBRIS);
            }

            Attributes.SHOP_SETTINGS.setValue(request, response, ShopSettingsUtil.createCookieWithSessionValues(channel, language, country,
                                                                                                                cookieMessageConfirmed, useIconView,
                                                                                                                useListView, useDetailView, autoApplyFilter,
                                                                                                                itemsPerPage));
        }
    }

    protected void removeAnonymousCheckout() {
        if (Boolean.TRUE.equals(sessionService.getAttribute(WebConstants.ANONYMOUS_CHECKOUT))) {
            sessionService.removeAttribute(WebConstants.ANONYMOUS_CHECKOUT);
        }
    }
}

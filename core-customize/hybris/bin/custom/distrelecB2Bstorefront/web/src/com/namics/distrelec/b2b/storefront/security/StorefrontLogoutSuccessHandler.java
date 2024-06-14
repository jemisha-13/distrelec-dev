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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.storefront.security.impl.DefaultGUIDCookieStrategy;

import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Handles the navigation on logout, see {@link SimpleUrlLogoutSuccessHandler}.
 */
public class StorefrontLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private GUIDCookieStrategy guidCookieStrategy;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private DefaultGUIDCookieStrategy ffCookieStrategy;

    @Override
    public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication)
            throws IOException, ServletException {

        getGuidCookieStrategy().deleteCookie(request, response);
        getFfCookieStrategy().deleteCookie(request, response);
        getSessionService().setAttribute(DistConstants.Session.PRODUCT_HIERARCHY_LOADED, Boolean.FALSE);
        getSessionService().setAttribute(DistConstants.Session.MAIN_NAV_FLUSH, Boolean.TRUE);
        getSessionService().removeAttribute(CheckoutCustomerStrategy.ANONYMOUS_CHECKOUT);
        getSessionService().removeAttribute(DistConstants.Session.IS_NEW_REGISTRATION);
        // SEO Cookies
        request.getSession().setAttribute(DistConstants.Session.LOG_OUT_PRESS, Boolean.TRUE.toString());
        request.getSession().setAttribute(WebConstants.LOGOUT_SUCCESS, Boolean.TRUE);

        getSessionService().removeAttribute("isMarketingProfileSet");
        // Delegate to default redirect behavior
        super.onLogoutSuccess(request, response, authentication);
    }

    @Override
    protected String determineTargetUrl(final HttpServletRequest request, final HttpServletResponse response) {
        String targetUrl = super.determineTargetUrl(request, response);

        if (StringUtils.startsWith(targetUrl, "https") && StringUtils.isNotBlank(getDefaultTargetUrl())) {
            targetUrl = getDefaultTargetUrl();
        }

        if ("false".equals(request.getParameter("active"))) {
            targetUrl += (targetUrl.contains("?") ? '&' : '?') + "active=false&logout=true";
        }

        return targetUrl;
    }

    protected GUIDCookieStrategy getGuidCookieStrategy() {
        return guidCookieStrategy;
    }

    @Required
    public void setGuidCookieStrategy(final GUIDCookieStrategy guidCookieStrategy) {
        this.guidCookieStrategy = guidCookieStrategy;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public DefaultGUIDCookieStrategy getFfCookieStrategy() {
        return ffCookieStrategy;
    }

    public void setFfCookieStrategy(DefaultGUIDCookieStrategy ffCookieStrategy) {
        this.ffCookieStrategy = ffCookieStrategy;
    }


}

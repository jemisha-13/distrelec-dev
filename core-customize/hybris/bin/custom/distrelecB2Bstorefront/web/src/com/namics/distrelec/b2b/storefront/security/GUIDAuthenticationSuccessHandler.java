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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Default implementation of {@link AuthenticationSuccessHandler}.
 */
public class GUIDAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger LOG = LogManager.getLogger(GUIDAuthenticationSuccessHandler.class);

    private GUIDCookieStrategy guidCookieStrategy;

    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private DistCustomerFacade b2bCustomerFacade;

    @Autowired
    private SessionService sessionService;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication)
                                                                                                                                                   throws IOException,
                                                                                                                                                   ServletException {
        b2bCustomerFacade.checkAndUpdateCustomer();
        // Check if the customer is active
        if (!checkActive(response, authentication)) {
            return;
        }
        getGuidCookieStrategy().setCookie(request, response);
        sessionService.setAttribute(WebConstants.LOGIN_SUCCESS, Boolean.TRUE);
        getAuthenticationSuccessHandler().onAuthenticationSuccess(request, response, authentication);
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

    protected GUIDCookieStrategy getGuidCookieStrategy() {
        return guidCookieStrategy;
    }

    /**
     * @param guidCookieStrategy
     *            the guidCookieStrategy to set
     */
    @Required
    public void setGuidCookieStrategy(final GUIDCookieStrategy guidCookieStrategy) {
        this.guidCookieStrategy = guidCookieStrategy;
    }

    protected AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
        return authenticationSuccessHandler;
    }

    /**
     * @param authenticationSuccessHandler
     *            the authenticationSuccessHandler to set
     */
    @Required
    public void setAuthenticationSuccessHandler(final AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }
}

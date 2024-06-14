/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.security.impl;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import javax.servlet.http.HttpServletRequest;

/**
 * DefaultRememberMeService used for Login remember me function
 * 
 * @author wspalinger, Namics AG
 * @since Distrelec 1.0
 */
public class DefaultRememberMeService extends TokenBasedRememberMeServices {

    private UserService userService;
    private CustomerFacade customerFacade;

    /**
     * Create a new instance of {@code DefaultRememberMeService}
     * 
     * @param key
     * @param userDetailsService
     */
    public DefaultRememberMeService(final String key, final UserDetailsService userDetailsService) {
        super(key, userDetailsService);
    }

    @Override
    protected Authentication createSuccessfulAuthentication(final HttpServletRequest request, final UserDetails user) {
        getUserService().setCurrentUser(getUserService().getUserForUID(user.getUsername()));

        getCustomerFacade().loginSuccess();

        final RememberMeAuthenticationToken auth = new RememberMeAuthenticationToken(getKey(), user, user.getAuthorities());
        auth.setDetails(getAuthenticationDetailsSource().buildDetails(request));
        return auth;
    }

    @Override
    protected String retrievePassword(final Authentication authentication) {
        return getUserService().getUserForUID(authentication.getPrincipal().toString()).getEncodedPassword();
    }

    public boolean isRememberMeCookieSet(final HttpServletRequest request) {
        if (getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
            return false;
        }

        final String rememberMeCookie = extractRememberMeCookie(request);
        if (rememberMeCookie == null || rememberMeCookie.length() == 0) {
            return false;
        }
        return true;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public CustomerFacade getCustomerFacade() {
        return customerFacade;
    }

    public void setCustomerFacade(final CustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }
}

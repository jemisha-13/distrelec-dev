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
package com.namics.distrelec.b2b.storefront.security.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.enums.RegistrationType;
import com.namics.distrelec.b2b.core.util.DistLogUtils;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.security.AutoLoginStrategy;
import com.namics.distrelec.b2b.storefront.security.DistStorefrontAuthenticationSuccessProvider;
import com.namics.distrelec.b2b.storefront.security.GUIDCookieStrategy;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;

/**
 * Default implementation of {@link AutoLoginStrategy}
 */
public class DefaultAutoLoginStrategy implements AutoLoginStrategy {
    private static final Logger LOG = LogManager.getLogger(DefaultAutoLoginStrategy.class);

    private AuthenticationManager authenticationManager;

    private CustomerFacade customerFacade;

    private GUIDCookieStrategy guidCookieStrategy;

    @Autowired
    private DistCustomerFacade b2bCustomerFacade;

    @Resource
    private DistStorefrontAuthenticationSuccessProvider distStorefrontAuthenticationSuccessProvider;

    @Override
    public void login(final String username, final String password, final HttpServletRequest request, final HttpServletResponse response,
                      final RegistrationType registrationType) {
        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        token.setDetails(new WebAuthenticationDetails(request));
        try {
            final Authentication authentication = getAuthenticationManager().authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            getCustomerFacade().loginSuccess();
            getGuidCookieStrategy().setCookie(request, response);

            // Update Customer
            String isMarketingCookieEnabled = Attributes.FF_ENSIGTHEN_MARKETING_COOKIE.getValueFromCookies(request);
            b2bCustomerFacade.updateMarketingCookieConsent(isMarketingCookieEnabled == "1");
            b2bCustomerFacade.storeIPAddress();
            if (null != registrationType && null != registrationType.getCode()
                    && RegistrationType.CHECKOUT.getCode().equalsIgnoreCase(registrationType.getCode())) {
                distStorefrontAuthenticationSuccessProvider.onAuthenticationSuccess(request, response, authentication);
            }
        } catch (final Exception e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            DistLogUtils.logError(LOG, "{} {} Failure during autoLogin", e, ErrorLogCode.LOGIN_ERROR, ErrorSource.HYBRIS);
        }
    }

    protected AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    @Required
    public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    protected CustomerFacade getCustomerFacade() {
        return customerFacade;
    }

    @Required
    public void setCustomerFacade(final CustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }

    protected GUIDCookieStrategy getGuidCookieStrategy() {
        return guidCookieStrategy;
    }

    @Required
    public void setGuidCookieStrategy(final GUIDCookieStrategy guidCookieStrategy) {
        this.guidCookieStrategy = guidCookieStrategy;
    }
}

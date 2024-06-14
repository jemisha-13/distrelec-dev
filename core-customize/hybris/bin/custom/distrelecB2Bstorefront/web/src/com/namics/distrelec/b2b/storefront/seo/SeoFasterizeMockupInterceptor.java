/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.seo;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.storefront.interceptors.AbstractSeoInterceptor;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author datneerajs, Elfa Distrelec AB
 * @since Namics Extensions 1.0
 *
 */
public class SeoFasterizeMockupInterceptor extends AbstractSeoInterceptor {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private SessionService sessionService;

    @Override
    protected boolean preHandleOnce(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        final String path = getUrlPathHelper().getServletPath(request);
        // DISTRELEC-8334
        if (!getExcludeUrls().contains(path)) {
            if (isPostRequest(request)) { // Scenario 4 of specification
               // response.setHeader(CACHE_CONTROL, PRIVATE_MAX_AGE);
                addAdditionalCookies(request, response);
                return true;
            }

            if (isLogINButtonPressed(request)) { // LogIn Scenario(7) of specification
                //response.setHeader(CACHE_CONTROL, PRIVATE_MAX_AGE);
                addAdditionalCookies(request, response);
                request.getSession().removeAttribute(DistConstants.Session.LOG_IN_PRESS);
                return true;
            }

            if (isLogOutButtonPressed(request)) { // Logout Scenario of specification
                addAdditionalCookies(request, response);
                request.getSession().removeAttribute(DistConstants.Session.LOG_OUT_PRESS);
                return true;
            }

            adjustResponseHeaderAndCookies(isCookiesVariationAvailable(request), isLoggedIn(), response, request);
        }
        return true;
    }

    private boolean isLogINButtonPressed(final HttpServletRequest request) {
        return request.getSession().getAttribute(DistConstants.Session.LOG_IN_PRESS) != null;
    }

    private boolean isLogOutButtonPressed(final HttpServletRequest request) {
        return request.getSession().getAttribute(DistConstants.Session.LOG_OUT_PRESS) != null;
    }

    private void adjustResponseHeaderAndCookies(final boolean isCookiesVariationAvailable, final boolean isUserLoggedIn, final HttpServletResponse response,
            final HttpServletRequest request) {

        if ((isCookiesVariationAvailable == false) && (isUserLoggedIn == false)) { // Scenario 1 of specification
            //response.setHeader(CACHE_CONTROL, PRIVATE_MAX_AGE);
            addAdditionalCookies(request, response);
            return;
        }

   

    }

    private String getMaxAgeTimeConfiguration() {
        return getConfigurationService().getConfiguration().getString("fasterize.cache.ttl");
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public SessionService getSessionService() {
        return sessionService;
    }

    @Override
    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

}

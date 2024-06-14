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

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.storefront.interceptors.AbstractSeoInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor to additionally secure HTTPS calls. If no secure cookie is found the interceptor will redirect to the configured loginUrl.
 * The secure cookie is first generated after successful login on an HTTPS channel and sent with every following secure request.
 */
public class GUIDInterceptor extends AbstractSeoInterceptor {
    private static final Logger LOG = LogManager.getLogger(GUIDInterceptor.class);

    public static final String SECURE_GUID_SESSION_KEY = "acceleratorSecureGUID";

    @Override
    protected boolean preHandleOnce(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception { // NOPMD
        final String path = getUrlPathHelper().getServletPath(request);
        if (request.isSecure() && !getExcludeUrls().contains(path) && isLoggedIn()) {
            boolean redirect = true;
            final String guid = (String) request.getSession().getAttribute(SECURE_GUID_SESSION_KEY);
            if (guid != null && request.getCookies() != null) {
                final String guidCookieName = getCookieGenerator().getCookieName();
                if (guidCookieName != null) {
                    for (final Cookie cookie : request.getCookies()) {
                        if (guidCookieName.equals(cookie.getName())) {
                            if (guid.equals(cookie.getValue())) {
                                redirect = false;
                                break;
                            } else {
                                LOG.info("{} {} Found secure cookie with invalid value. expected [{}] actual[{}]removing.", ErrorLogCode.SESSION_COOKIE_ERROR,
                                        ErrorSource.HYBRIS, guid, cookie.getValue());
                                getCookieGenerator().removeCookie(response);
                            }
                        }
                    }
                }
            }
            if (redirect) {
                if (getDefaultRememberMeService().isRememberMeCookieSet(request)) {
                    getGuidCookieStrategy().setCookie(request, response);
                    return true;
                }
                LOG.warn((guid == null ? " {} {} missing secure token in session, redirecting" : "{} {} no matching guid cookie, redirecting"),
                        ErrorLogCode.SESSION_COOKIE_ERROR, ErrorSource.HYBRIS);
                getRedirectStrategy().sendRedirect(request, response, getLoginUrl());
                return false;
            }
        }

        return true;
    }

}

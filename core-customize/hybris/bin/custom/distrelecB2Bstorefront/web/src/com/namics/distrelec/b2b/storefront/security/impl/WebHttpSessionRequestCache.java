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

import com.namics.distrelec.b2b.facades.constants.WebConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Extension of HttpSessionRequestCache that allows pass through of cookies from the current request. This is required to allow the
 * GUIDInterceptor to see the secure cookie written during authentication.
 * 
 * The <tt>RequestCache</tt> stores the <tt>SavedRequest</tt> in the HttpSession, this is then restored perfectly. Unfortunately the saved
 * request also hides new cookies that have been written since the saved request was created. This implementation allows the current
 * request's cookie values to override the cookies within the saved request.
 */
public class WebHttpSessionRequestCache extends HttpSessionRequestCache {

    private static final String REFERER = "referer";

    static final String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

    private PortResolver portResolver = new PortResolverImpl();
    private RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;
    private boolean createSessionAllowed = true;

    @Override
    public HttpServletRequest getMatchingRequest(final HttpServletRequest request, final HttpServletResponse response) {
        HttpServletRequest result = super.getMatchingRequest(request, response);
        if (result != null) {
            result = new CookieMergingHttpServletRequestWrapper(result, request);
        }
        return result;
    }

    /**
     * Request wrapper that wraps an innerRequest, and overlays on top of it the cookies from the outerRequest.
     */
    public static class CookieMergingHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private final HttpServletRequest outerRequest;

        public CookieMergingHttpServletRequestWrapper(final HttpServletRequest innerRequest, final HttpServletRequest outerRequest) {
            super(innerRequest);
            this.outerRequest = outerRequest;
        }

        @Override
        public Cookie[] getCookies() {
            return mergeCookies(super.getCookies(), outerRequest.getCookies());
        }

        protected Cookie[] mergeCookies(final Cookie[] savedCookies, final Cookie[] currentCookies) {
            // Start with the cookies from the current request as these will be more up to date
            final List<Cookie> cookies = new ArrayList<Cookie>(Arrays.asList(currentCookies));
            // Add any missing ones from the saved request
            Stream.<Cookie> of(savedCookies).filter(savedCookie -> !containsCookie(cookies, savedCookie.getName()))
                    .forEach(savedCookie -> cookies.add(savedCookie));

            return cookies.toArray(new Cookie[cookies.size()]);
        }

        protected boolean containsCookie(final List<Cookie> cookies, final String cookieName) {
            if (cookies == null || cookies.isEmpty() || cookieName == null) {
                return false;
            }

            return cookies.stream().anyMatch(cookie -> StringUtils.equals(cookieName, cookie.getName()));
        }
    }

    @Override
    public void saveRequest(final HttpServletRequest request, final HttpServletResponse response) {
        // this might be called while in ExceptionTranslationFilter#handleSpringSecurityException in this case base implementation
        // if the request uri from the request is equal to the spring security check url, the user failed the login through meta
        // login or login through the lightbox and therefore we can use our implementation
        if (SecurityContextHolder.getContext().getAuthentication() == null
                && (!StringUtils.equals(request.getRequestURI(), WebConstants.SPRING_SECURITY_CHECK_URL))) {
            super.saveRequest(request, response);
        } else {
            final SavedRequest savedBefore = getRequest(request, response);
            if (savedBefore != null)// to not override request saved by ExceptionTranslationFilter#handleSpringSecurityException
            {
                return;
            }

            if (getRequestMatcher().matches(request)) {
                final DefaultSavedRequest savedRequest = new DefaultSavedRequest(request, getPortResolver()) {
                    private final String referer = request.getHeader(REFERER);

                    @Override
                    public String getRedirectUrl() {
                        return referer;
                    }
                };

                if (isCreateSessionAllowed() || request.getSession(false) != null) {
                    request.getSession().setAttribute(SAVED_REQUEST, savedRequest);
                    logger.debug("DefaultSavedRequest added to Session: " + savedRequest);
                }
            } else {
                logger.debug("Request not saved as configured RequestMatcher did not match");
            }
        }
    }

    @Override
    public void setRequestMatcher(final RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
        super.setRequestMatcher(requestMatcher);
    }

    @Override
    public void setPortResolver(final PortResolver portResolver) {
        this.portResolver = portResolver;
        super.setPortResolver(portResolver);
    }

    @Override
    public void setCreateSessionAllowed(final boolean createSessionAllowed) {
        this.createSessionAllowed = createSessionAllowed;
    }

    protected boolean isCreateSessionAllowed() {
        return createSessionAllowed;
    }

    protected PortResolver getPortResolver() {
        return portResolver;
    }

    protected RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }
}

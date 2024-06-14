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
package com.namics.distrelec.b2b.storefront.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Abstract base class that calls a perHandle method once per request. During a request there may be multiple HandlerInterceptorAdapter
 * perHandle calls, one per each include or forward.
 */
public abstract class AbstractOncePerRequestPreHandleInterceptor extends HandlerInterceptorAdapter {
    public final String INTERCEPTOR_ONCE_KEY = String.valueOf(this).intern();

    @Override
    public final boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception { // NOPMD
        boolean result = true;

        if (request.getAttribute(INTERCEPTOR_ONCE_KEY) == null) {
            // Call the pre handler once for the request
            result = preHandleOnce(request, response, handler);
            request.setAttribute(INTERCEPTOR_ONCE_KEY, Boolean.TRUE);
        }

        return result;
    }

    /**
     * Intercept the execution of a handler. Called after HandlerMapping determined an appropriate handler object, but before HandlerAdapter
     * invokes the handler.
     * <p>
     * DispatcherServlet processes a handler in an execution chain, consisting of any number of interceptors, with the handler itself at the
     * end. With this method, each interceptor can decide to abort the execution chain, typically sending a HTTP error or writing a custom
     * response.
     *
     * @param request
     *            current HTTP request
     * @param response
     *            current HTTP response
     * @param handler
     *            chosen handler to execute, for type and/or instance evaluation
     * @return <code>true</code> if the execution chain should proceed with the next interceptor or the handler itself. Else,
     *         DispatcherServlet assumes that this interceptor has already dealt with the response itself.
     * @throws Exception
     *             in case of errors
     */
    protected abstract boolean preHandleOnce(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception; // NOPMD

}

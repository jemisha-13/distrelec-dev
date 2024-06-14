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
package com.namics.distrelec.b2b.storefront.controllers;

import com.namics.distrelec.b2b.core.util.ErpStatusUtil;
import de.hybris.platform.acceleratorservices.util.SpringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * Base controller for all controllers. Provides common functionality for all controllers.
 */
public abstract class AbstractController {
    public static final String REDIRECT_PREFIX = "redirect:";
    public static final String REDIRECT_PERMANENT_PREFIX = "redirect301:";
    public static final String FORWARD_PREFIX = "forward:";
    public static final String ROOT = "/";
    public static final String QUESTION_MARK = "?";
    public static final String AMPERSAND = "&";
    public static final String NO_CACHE_EQUALS_TRUE = "no-cache=true";
    public static final String QUESTION_MARK_NO_CACHE_EQUALS_TRUE = QUESTION_MARK + NO_CACHE_EQUALS_TRUE;
    public static final String AMPERSAND_NO_CACHE_EQUALS_TRUE = AMPERSAND + NO_CACHE_EQUALS_TRUE;
    public static final String REGISTRATION = "/registration";

    @Autowired
    protected ErpStatusUtil erpStatusUtil;

    @ModelAttribute("request")
    public HttpServletRequest addRequestToModel(final HttpServletRequest request) {
        return request;
    }

    public static String addFasterizeCacheControlParameter(final String url) {
        if (url.contains(NO_CACHE_EQUALS_TRUE))
            return url;

        if (url.contains(QUESTION_MARK))
            return url + AMPERSAND_NO_CACHE_EQUALS_TRUE;

        return url + QUESTION_MARK_NO_CACHE_EQUALS_TRUE;
    }

    protected <T> T getBean(final HttpServletRequest request, final String beanName, final Class<T> beanType) {
        return SpringHelper.getSpringBean(request, beanName, beanType, true);
    }

    /**
     * HttpNotFoundException.
     *
     */
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class HttpNotFoundException extends RuntimeException {
        /**
         * Default constructor
         */
        public HttpNotFoundException() {
            super();
        }

        /**
         * Alternative constructor using message and cause.
         *
         * @param message
         *            the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
         * @param cause
         *            the cause (which is saved for later retrieval by the {@link #getCause()} method). (A <tt>null</tt> value is permitted,
         *            and indicates that the cause is nonexistent or unknown.)
         */
        public HttpNotFoundException(final String message, final Throwable cause) {
            super(message, cause);
        }

        /**
         * Alternative constructor using message.
         *
         * @param message
         *            the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
         */
        public HttpNotFoundException(final String message) {
            super(message);
        }

        /**
         * Alternative constructor using cause.
         *
         * @param cause
         *            the cause (which is saved for later retrieval by the {@link #getCause()} method). (A <tt>null</tt> value is permitted,
         *            and indicates that the cause is nonexistent or unknown.)
         */
        public HttpNotFoundException(final Throwable cause) {
            super(cause);
        }
    }

    public ErpStatusUtil getErpStatusUtil() {
        return erpStatusUtil;
    }

    public void setErpStatusUtil(ErpStatusUtil erpStatusUtil) {
        this.erpStatusUtil = erpStatusUtil;
    }
}

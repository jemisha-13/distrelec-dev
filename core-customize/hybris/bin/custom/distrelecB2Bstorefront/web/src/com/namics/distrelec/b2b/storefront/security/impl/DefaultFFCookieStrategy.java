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

import com.namics.distrelec.b2b.storefront.security.GUIDCookieStrategy;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.util.CookieGenerator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Default implementation of {@link GUIDCookieStrategy}
 */
public class DefaultFFCookieStrategy implements GUIDCookieStrategy {
    private static final Logger LOG = Logger.getLogger(DefaultFFCookieStrategy.class);

    @Resource(name = "ffSessionIdGenerator")
    private KeyGenerator ffSessionIdGenerator;

    private CookieGenerator cookieGenerator;

    @Override
    public void setCookie(final HttpServletRequest request, final HttpServletResponse response) {
        if (!request.isSecure()) {
            // We must not generate the cookie for insecure requests, otherwise there is not point doing this at all
            throw new IllegalStateException("Cannot set GUIDCookie on an insecure request!");
        }

        final String guid = (String) ffSessionIdGenerator.generate();
        getCookieGenerator().addCookie(response, guid);
        
        if (LOG.isInfoEnabled()) {
            LOG.info("Setting guid cookie and session attribute: " + guid);
        }
    }

    @Override
    public void deleteCookie(final HttpServletRequest request, final HttpServletResponse response) {
        if (!request.isSecure()) {
            LOG.error("Cannot remove secure GUIDCookie during an insecure request. I should have been called from a secure page.");
        } else {
            // Its a secure page, we can delete the cookie
            getCookieGenerator().removeCookie(response);
        }
    }

    protected CookieGenerator getCookieGenerator() {
        return cookieGenerator;
    }

    @Required
    public void setCookieGenerator(final CookieGenerator cookieGenerator) {
        this.cookieGenerator = cookieGenerator;
    }

    protected KeyGenerator getFFSessionIdGenerator() {
        return ffSessionIdGenerator;
    }

    protected void setFFSessionIdGenerator(KeyGenerator ffSessionIdGenerator) {
        this.ffSessionIdGenerator = ffSessionIdGenerator;
    }
}

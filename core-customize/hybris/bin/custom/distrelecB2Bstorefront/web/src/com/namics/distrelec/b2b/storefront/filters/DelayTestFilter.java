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
package com.namics.distrelec.b2b.storefront.filters;

import com.namics.distrelec.b2b.facades.constants.WebConstants;
import org.apache.log4j.Logger;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A filter that delay the request/response.
 */
public class DelayTestFilter extends GenericFilterBean {

    private static final Logger LOG = Logger.getLogger(DelayTestFilter.class.getName());

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException,
            ServletException {
        long delayTimeInMillis = 0;
        if (servletRequest instanceof HttpServletRequest) {
            final String fixDelayTimeInMillis = servletRequest.getParameter(WebConstants.TEST_FIX_DELAY_IN_MS);
            final String randomDelayTimeInMillis = servletRequest.getParameter(WebConstants.TEST_RANDOM_DELAY_IN_MS);
            try {
                if (fixDelayTimeInMillis != null) {
                    LOG.debug(WebConstants.TEST_FIX_DELAY_IN_MS + ": " + fixDelayTimeInMillis + " ms");
                    delayTimeInMillis = Long.parseLong(fixDelayTimeInMillis);
                } else if (randomDelayTimeInMillis != null) {
                    LOG.debug(WebConstants.TEST_RANDOM_DELAY_IN_MS + ": " + randomDelayTimeInMillis + " ms");
                    delayTimeInMillis = (long) (Math.random() * Long.parseLong(randomDelayTimeInMillis));
                }
            } catch (final NumberFormatException nfe) {
                LOG.info("Cannot parse number " + nfe.getMessage());
            }
        }

        if (delayTimeInMillis > 0) {
            LOG.debug("Delay response for " + delayTimeInMillis + " ms...");
            try {
                Thread.sleep(delayTimeInMillis);
            } catch (final InterruptedException e) {
                LOG.warn("Delay response for " + delayTimeInMillis + " ms FAILED");
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}

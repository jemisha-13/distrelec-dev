/*
 * Copyright 2000-2017 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.handlerinterceptors.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.fest.util.Maps;
import org.springframework.web.servlet.ModelAndView;

import com.namics.distrelec.b2b.storefront.handlerinterceptors.AbstractPerformanceHandlerInterceptor;

/**
 * 
 * {@code AnalysingModelHandlerInterceptor}
 * 
 * @author mab@foryouandyourcustomers.com - 23 Mar 2017
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.15
 */
public class AnalysingModelHandlerInterceptor extends AbstractPerformanceHandlerInterceptor {

    private static final Logger LOG = Logger.getLogger(AnalysingModelHandlerInterceptor.class);

    private static final String NEW_LINE = "\n";

    /**
     * This method is executed after the handler is executed
     */
    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView)
            throws Exception {
        if (!isTimeTrackingEnable()) {
            return;
        }

        final Map<String, Object> modelMap = modelAndView.getModel();

        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.ensureCapacity(modelMap.entrySet().size() + 13);
        strBuilder.append("PostHandle - IP[").append(getIpAddress(request)).append("] Uri[").append(getUri(request)).append("] UrlQuery[")
                .append(getUrlQuery(request)).append("] HandleName[").append(getHandlerName(handler)).append("] - ");

        final String msg = "Model map injected into View[" + getViewName(modelAndView) + "]";
        if (!Maps.isEmpty(modelMap)) {
            strBuilder.append(msg);
            strBuilder.append(": ");
            strBuilder.append(NEW_LINE);
            for (final Map.Entry<String, Object> entry : modelMap.entrySet()) {
                appendEntry(strBuilder, entry);
            }
        } else {
            strBuilder.append("No ").append(msg);
        }
        LOG.info(strBuilder.toString());
    }

    /**
     * 
     * @param strBuilder
     * @param entry
     */
    protected <K, V> void appendEntry(final StringBuilder strBuilder, final Map.Entry<K, V> entry) {
        strBuilder.ensureCapacity(6);
        strBuilder.append("\t key[").append(entry.getKey()).append("] value[").append(entry.getValue()).append("]").append(NEW_LINE);
    }
}

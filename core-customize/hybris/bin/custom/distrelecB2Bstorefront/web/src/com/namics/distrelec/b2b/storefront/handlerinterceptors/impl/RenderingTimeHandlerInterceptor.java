/*
 * Copyright 2000-2017 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.handlerinterceptors.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.namics.distrelec.b2b.storefront.handlerinterceptors.AbstractPerformanceHandlerInterceptor;
import com.namics.distrelec.b2b.storefront.util.TimingUtils;

/**
 * {@code RenderingTimeHandlerInterceptor}
 * 
 * @author mab@foryouandyourcustomers.com - 23 Mar 2017
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.15
 */
public class RenderingTimeHandlerInterceptor extends AbstractPerformanceHandlerInterceptor {

    private static final Logger LOG = LogManager.getLogger(RenderingTimeHandlerInterceptor.class);

    private static final String HANDLE_TIME_START_ATTRIBUTENAME = "handlingTimeStart";
    private static final String RENDER_TIME_START_ATTRIBUTENAME = "renderingTimeStart";
    private static final String VIEW_NAME_START_ATTRIBUTENAME = "distrelecViewName";

    private boolean nanoPrecision = true;

    /**
     * This method is executed before the handler will be executed
     */
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        if (isTimeTrackingEnable()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("PreHandle - IP[" + getIpAddress(request) + "] Uri[" + getUri(request) + "] UrlQuery[" + getUrlQuery(request) + "] HandleName["
                        + getHandlerName(handler) + "]");
            }

            saveCurrentTime(request, HANDLE_TIME_START_ATTRIBUTENAME);
        }
        return true;
    }

    /**
     * This method is executed after the handler is executed
     */
    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView)
            throws Exception {
        if (!isTimeTrackingEnable()) {
            return;
        }

        final long handlingTime = calculateExecutionTime(request, HANDLE_TIME_START_ATTRIBUTENAME);

        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("PostHandle - IP[") //
                .append(getIpAddress(request)) //
                .append("] Uri[") //
                .append(getUri(request)) //
                .append("] UrlQuery[") //
                .append(getUrlQuery(request)) //
                .append("] HandleName[") //
                .append(getHandlerName(handler))//
                .append("] - Handling time ")//
                .append(TimingUtils.nanosToMillis(handlingTime))//
                .append(" ms");
        LOG.info(strBuilder.toString());

        saveCurrentTime(request, RENDER_TIME_START_ATTRIBUTENAME);
        request.setAttribute(VIEW_NAME_START_ATTRIBUTENAME, getViewName(modelAndView));
    }

    /**
     * This method is executed after the view is rendered and before the http response is sent
     */
    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex)
            throws Exception {
        if (!isTimeTrackingEnable()) {
            return;
        }

        final long renderingTime = calculateExecutionTime(request, RENDER_TIME_START_ATTRIBUTENAME);

        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("AfterCompletion - IP[") //
                .append(getIpAddress(request)) //
                .append("] Uri[") //
                .append(getUri(request)) //
                .append("] UrlQuery[") //
                .append(getUrlQuery(request)) //
                .append("] RenderView[") //
                .append(request.getAttribute(VIEW_NAME_START_ATTRIBUTENAME)) //
                .append("] - Rendering time ") //
                .append(TimingUtils.nanosToMillis(renderingTime)).append(" ms");
        LOG.info(strBuilder.toString());
    }

    /**
     * @param request
     * @param attributeName
     */
    protected void saveCurrentTime(final HttpServletRequest request, final String attributeName) {
        final long timeNow = TimingUtils.startTimer(isNanoPrecision());
        request.setAttribute(attributeName, Long.valueOf(timeNow));
    }

    /**
     * 
     * @param request
     * @param attributeName
     * @return
     */
    protected long calculateExecutionTime(final HttpServletRequest request, final String attributeName) {
        final Object attributeObj = request.getAttribute(attributeName);
        if (attributeObj != null) {
            final long startTime = ((Long) attributeObj).longValue();
            return TimingUtils.stopTimer(startTime, isNanoPrecision());
        }
        return -1;
    }

    public boolean isNanoPrecision() {
        return nanoPrecision;
    }

    public void setNanoPrecision(final boolean nanoPrecision) {
        this.nanoPrecision = nanoPrecision;
    }
}

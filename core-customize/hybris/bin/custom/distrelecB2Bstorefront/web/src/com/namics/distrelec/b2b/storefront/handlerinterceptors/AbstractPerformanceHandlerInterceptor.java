/*
 * Copyright 2000-2017 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.handlerinterceptors;

import javax.servlet.http.HttpServletRequest;

import org.fest.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * {@code AbstractPerformanceHandlerInterceptor}
 * 
 *
 * @author mab@foryouandyourcustomers.com - 23 Mar 2017
 * @since Distrelec 5.15
 */
public abstract class AbstractPerformanceHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final String TIME_TRACKING_ENABLED_KEY = "distrelec.frontend.time.tracking";

    private static final String IP_ADDRESS_HEADER_ATTRIBUTENAME = "X-FORWARDED-FOR";

    private static final String NO_NAME_RETURNVALUE = "no-name";
    private static final String NO_VIEW_RETURNVALUE = "no-view";

    @Autowired
    private ConfigurationService configurationService;

    /**
     * 
     * @param request
     * @return
     */
    protected String getIpAddress(final HttpServletRequest request) {
        // Check if the request comes from a proxy or a load balancer
        final String ipAddress = request.getHeader(IP_ADDRESS_HEADER_ATTRIBUTENAME);
        if (Strings.isEmpty(ipAddress)) {
            return request.getRemoteAddr();
        }
        return ipAddress;
    }

    /**
     * 
     * @param request
     * @return
     */
    protected String getUri(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    /**
     * 
     * @param request
     * @return
     */
    protected String getUrlQuery(final HttpServletRequest request) {
        return request.getQueryString();
    }

    /**
     * 
     * @param handler
     * @return
     */
    protected String getHandlerName(final Object handler) {
        HandlerMethod handlerMethod = null;
        if (handler instanceof HandlerMethod) {
            handlerMethod = (HandlerMethod) handler;
        }
        if (handlerMethod != null) {
            final Object handlerBean = handlerMethod.getBean();
            if (handlerBean != null) {
                return handlerBean.getClass().getSimpleName();
            }
            final Class<?> handlerBeanType = handlerMethod.getBeanType();
            if (handlerBeanType != null) {
                return handlerBeanType.getSimpleName();
            }
        }
        return NO_NAME_RETURNVALUE;
    }

    /**
     * 
     * @param modelAndView
     * @return
     */
    protected String getViewName(final ModelAndView modelAndView) {
        if (modelAndView.hasView()) {
            return modelAndView.getViewName();
        }
        return NO_VIEW_RETURNVALUE;
    }

    public boolean isTimeTrackingEnable() {
        return getConfigurationService().getConfiguration().getBoolean(TIME_TRACKING_ENABLED_KEY, false);
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}

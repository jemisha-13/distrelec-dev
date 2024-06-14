/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.namics.distrelec.b2b.storefront.web.view.UiExperienceViewResolver;

import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Filter to enable or disable terrific support for pages and components.
 * 
 * @author jweiss, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class NamicsSwitchTerrificViewHandlerInterceptorAdapter extends HandlerInterceptorAdapter {
    private static final Logger LOG = Logger.getLogger(NamicsSwitchTerrificViewHandlerInterceptorAdapter.class);

    public static final String CONFIG_PROPERTY_TERRIFIC_SUPPORT = "terrific.support.enabled";
    @Autowired
    protected ConfigurationService configurationService;

    protected final static String PAGE_WITHOUT_TERRIFIC = "pages/";
    protected final static String PAGE_WITH_TERRIFIC = "pages/terrific/";

    protected final static String COMPONENT_WITHOUT_TERRIFIC = "cms/";
    protected final static String COMPONENT_WITH_TERRIFIC = "cms/terrific/";

    protected final static String FRAGMENTS_WITHOUT_TERRIFIC = "fragments/";
    protected final static String FRAGMENTS_WITH_TERRIFIC = "fragments/terrific/";

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView) {
        // Check to see if we are using the default view for the page
        if (modelAndView != null && !isRedirectView(modelAndView)) {
            final String viewForPage = modelAndView.getViewName();
            if (viewForPage != null && configurationService.getConfiguration().getBoolean(CONFIG_PROPERTY_TERRIFIC_SUPPORT, false)) {
                final String viewForTerrificPage = viewForPage.replace(PAGE_WITHOUT_TERRIFIC, PAGE_WITH_TERRIFIC)
                        .replace(COMPONENT_WITHOUT_TERRIFIC, COMPONENT_WITH_TERRIFIC).replace(FRAGMENTS_WITHOUT_TERRIFIC, FRAGMENTS_WITH_TERRIFIC);

                LOG.debug("Changing view from [" + viewForPage + "] to terrific view [" + viewForTerrificPage + "]");
                modelAndView.setViewName(viewForTerrificPage);
            }
        }
    }

    protected boolean isRedirectView(final ModelAndView modelAndView) {
        final String viewName = modelAndView.getViewName();
        return viewName != null
                && (viewName.startsWith(UrlBasedViewResolver.REDIRECT_URL_PREFIX) || viewName
                        .startsWith(UiExperienceViewResolver.REDIRECT_PERMANENT_URL_PREFIX));
    }

}

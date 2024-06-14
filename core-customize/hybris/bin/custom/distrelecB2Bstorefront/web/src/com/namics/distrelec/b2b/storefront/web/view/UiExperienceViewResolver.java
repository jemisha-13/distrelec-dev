package com.namics.distrelec.b2b.storefront.web.view;

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

import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.*;

import java.util.Locale;
import java.util.Map;

/**
 * A view resolver that detects the device a request is coming from and directs it to the appropriate view. This view resolver extends
 * Spring's org.springframework.web.servlet.view.InternalResourceViewResolver.
 */
public class UiExperienceViewResolver extends InternalResourceViewResolver {
    private static final Logger LOG = Logger.getLogger(UiExperienceViewResolver.class);

    public final static String REDIRECT_PERMANENT_URL_PREFIX = "redirect301:";

    private UiExperienceService uiExperienceService;
    private Map<UiExperienceLevel, String> uiExperienceViewPrefix;
    private String unknownUiExperiencePrefix;

    protected UiExperienceService getUiExperienceService() {
        return uiExperienceService;
    }

    @Required
    public void setUiExperienceService(final UiExperienceService uiExperienceService) {
        this.uiExperienceService = uiExperienceService;
    }

    protected Map<UiExperienceLevel, String> getUiExperienceViewPrefix() {
        return uiExperienceViewPrefix;
    }

    @Required
    public void setUiExperienceViewPrefix(final Map<UiExperienceLevel, String> uiExperienceViewPrefix) {
        this.uiExperienceViewPrefix = uiExperienceViewPrefix;
    }

    protected String getUnknownUiExperiencePrefix() {
        return unknownUiExperiencePrefix;
    }

    @Required
    public void setUnknownUiExperiencePrefix(final String unknownUiExperiencePrefix) {
        this.unknownUiExperiencePrefix = unknownUiExperiencePrefix;
    }

    @Override
    protected AbstractUrlBasedView buildView(final String viewName) throws Exception {
        final UiExperienceLevel uiExperienceLevel = getUiExperienceService().getUiExperienceLevel();
        final String expandedViewName = getViewName(uiExperienceLevel, viewName);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Expanded View Name [" + viewName + "] into [" + expandedViewName + "]");
        }

        final InternalResourceView view = (InternalResourceView) super.buildView(expandedViewName);
        view.setAlwaysInclude(false);
        return view;
    }

    public String getViewName(final UiExperienceLevel uiExperienceLevel, final String viewName) {
        final String prefix = getUiExperienceViewPrefix().get(uiExperienceLevel);
        if (prefix != null) {
            return prefix + viewName;
        }

        return getUnknownUiExperiencePrefix() + viewName;
    }

    @Override
    protected View createView(final String viewName, final Locale locale) throws Exception {

        // If this resolver is not supposed to handle the given view,
        // return null to pass on to the next resolver in the chain.
        if (!canHandle(viewName, locale)) {
            return null;
        }
        // Check for special "redirect:" prefix.
        if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
            final String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length());
            final RedirectView view = new RedirectView(redirectUrl, isRedirectContextRelative(), isRedirectHttp10Compatible());
            return applyLifecycleMethods(viewName, view);
        }

        // Check for special "redirect301:" prefix.
        if (viewName.startsWith(REDIRECT_PERMANENT_URL_PREFIX)) {
            final String redirectUrl = viewName.substring(REDIRECT_PERMANENT_URL_PREFIX.length());

            // create the redirect view explicitly avoiding the exposure of model attributes in the URL.
            final RedirectView view = new RedirectView(redirectUrl, isRedirectContextRelative(), isRedirectHttp10Compatible(), false);
            view.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            return applyLifecycleMethods(viewName, view);

        }

        // Check for special "forward:" prefix.
        if (viewName.startsWith(FORWARD_URL_PREFIX)) {
            final String forwardUrl = viewName.substring(FORWARD_URL_PREFIX.length());
            return new InternalResourceView(forwardUrl);
        }
        // Else fall back to superclass implementation: calling loadView.
        return super.createView(viewName, locale);

    }

    private View applyLifecycleMethods(final String viewName, final AbstractView view) {
        return (View) getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, viewName);
    }
}

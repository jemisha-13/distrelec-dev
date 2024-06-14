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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commerceservices.i18n.LanguageResolver;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * Allow the language to be changed per request.
 */
public class LanguageInterceptor extends AbstractOncePerRequestPreHandleInterceptor {
    private static final Logger LOG = Logger.getLogger(LanguageInterceptor.class);

    public static final String DEFAULT_LANG_PARAM = "lang";

    private String languageParameter = DEFAULT_LANG_PARAM;
    private LanguageResolver languageResolver;
    private CommonI18NService commonI18NService;

    protected String getLanguageParameter() {
        return languageParameter;
    }

    // Optional - defaults to DEFAULT_LANG_PARAM
    public void setLanguageParameter(final String paramKey) {
        this.languageParameter = paramKey;
    }

    protected CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    protected LanguageResolver getLanguageResolver() {
        return languageResolver;
    }

    @Required
    public void setLanguageResolver(final LanguageResolver languageResolver) {
        this.languageResolver = languageResolver;
    }

    @Override
    protected boolean preHandleOnce(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        if (isGetMethod(request)) {
            final String languageIdentifier = request.getParameter(languageParameter);
            if (StringUtils.isNotBlank(languageIdentifier)) {
                try {
                    final LanguageModel languageModel = getLanguageResolver().getLanguage(languageIdentifier);
                    getCommonI18NService().setCurrentLanguage(languageModel);
                } catch (final IllegalArgumentException ile) {
                    LOG.warn("Can not set session language to [" + languageIdentifier + "]. " + ile.getMessage());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Exception setting the language", ile);
                    }
                }
            }
        }
        return true;
    }

    protected boolean isGetMethod(final HttpServletRequest request) {
        return "GET".equalsIgnoreCase(request.getMethod());
    }
}
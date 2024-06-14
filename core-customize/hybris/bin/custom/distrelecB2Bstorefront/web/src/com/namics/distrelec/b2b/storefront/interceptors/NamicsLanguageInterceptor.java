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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import de.hybris.platform.commerceservices.i18n.LanguageResolver;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * Allow the language to be changed per request.
 */
public class NamicsLanguageInterceptor extends AbstractOncePerRequestPreHandleInterceptor {
    private static final Logger LOG = Logger.getLogger(NamicsLanguageInterceptor.class);

    public static final String DEFAULT_LANG_PARAM = "lang";

    private String languageParameter = DEFAULT_LANG_PARAM;
    private LanguageResolver languageResolver;
    private CommonI18NService commonI18NService;

    private Pattern localePattern;

    public void setLocalePattern(final String localePattern) {
        Assert.isTrue(localePattern.matches(".*\\(.*\\).*"), "Your pattern needs to define a match group");
        this.localePattern = Pattern.compile(localePattern);
    }

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
            String languageIdentifier = request.getParameter(languageParameter);

            final String pathTranslated = request.getRequestURI();
            if (pathTranslated != null) {
                final Matcher matcher = localePattern.matcher(pathTranslated);
                if (matcher.find()) {
                    languageIdentifier = matcher.group(1);
                }
            }

            if (StringUtils.isNotBlank(languageIdentifier)
                    && !DistConstants.UrlTags.PRODUCT_FAMILY.equals(languageIdentifier)) {
                try {
                    final LanguageModel languageModel = getLanguageResolver().getLanguage(languageIdentifier);
                    getCommonI18NService().setCurrentLanguage(languageModel);
                } catch (final IllegalArgumentException ile) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Can not set session language to [" + languageIdentifier + "]: " + ile.getMessage(), ile);
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
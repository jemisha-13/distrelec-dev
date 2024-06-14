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
package com.namics.distrelec.b2b.storefront.i18n;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Implementation of {@link LocaleResolver} which falls back to the locale defined by the current site.
 */
public class StoreSessionLocaleResolver implements LocaleResolver {

    public static final String LOCALE_REQUEST_ATTRIBUTE_NAME = StoreSessionLocaleResolver.class.getName() + ".LOCALE";

    private I18NService i18NService;
    private CMSSiteService cmsSiteService;

    @Override
    public Locale resolveLocale(final HttpServletRequest request) {
        // Lookup the cached locale in the request
        Locale locale = (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);

        if (locale == null) {
            // Get the locale from the hybris session
            locale = getHybrisSessionLocale();

            // Cache the locale in the request attributes
            if (locale != null) {
                locale = appendCountryIfNecessary(locale);
                request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale);
            }

            return locale;
        } else {
            return appendCountryIfNecessary(locale);
        }
    }

    protected Locale getHybrisSessionLocale() {
        Locale sessionLocale = getI18NService().getCurrentLocale();
        if (sessionLocale == null) {
            final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
            if (currentSite != null) {
                final String siteLocale = currentSite.getLocale();
                if (StringUtils.isNotBlank(siteLocale)) {
                    final String[] locale = siteLocale.split("_");
                    if (locale.length == 1) {
                        sessionLocale = new Locale(locale[0]);
                    } else if (locale.length == 2) {
                        sessionLocale = new Locale(locale[0], locale[1]);
                    }
                }
            }
        }

        return sessionLocale;
    }

    protected Locale appendCountryIfNecessary(final Locale locale) {
        Locale locLocale = locale;
        if (locLocale != null) {
            if (StringUtils.isBlank(locLocale.getCountry())) {
                final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
                if (currentSite != null) {
                    final String siteLocale = currentSite.getLocale();
                    if (StringUtils.isNotBlank(siteLocale)) {
                        final String[] localString = siteLocale.split("_");
                        if (localString.length == 2) {
                            locLocale = new Locale(locLocale.getLanguage(), localString[1]);
                        } else {
                            locLocale = new Locale(locLocale.getLanguage(), currentSite.getCountry() != null ? currentSite.getCountry().getIsocode() : "");
                        }
                    } else {
                        locLocale = new Locale(locLocale.getLanguage(), currentSite.getCountry() != null ? currentSite.getCountry().getIsocode() : "");
                    }
                }
            }
        }
        return locLocale;
    }

    @Override
    public void setLocale(final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {
        // Ignore setting the locale as it must be changed only via the hybris session
    }

    public I18NService getI18NService() {
        return i18NService;
    }

    @Required
    public void setI18NService(final I18NService i18NService) {
        this.i18NService = i18NService;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }
}

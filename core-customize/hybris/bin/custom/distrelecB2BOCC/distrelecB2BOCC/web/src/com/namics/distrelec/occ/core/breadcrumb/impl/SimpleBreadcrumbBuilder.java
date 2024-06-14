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
package com.namics.distrelec.occ.core.breadcrumb.impl;

import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;

/**
 * SimpleBreadcrumbBuilder implementation for simple cases with title displayed at the end of breadcrumb
 */
public class SimpleBreadcrumbBuilder {
    private static final String LAST_LINK_CLASS = "active";

    private I18NService i18nService;

    private MessageSource messageSource;

    public List<Breadcrumb> getBreadcrumbsFromProperty(final String resourceKey) {
        final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
        if (StringUtils.isNotEmpty(resourceKey)) {
            final String message = getMessageSource().getMessage(resourceKey, null, resourceKey, getI18nService().getCurrentLocale());
            final String messageEN = getMessageSource().getMessage(resourceKey, null, resourceKey, Locale.ENGLISH);
            breadcrumbs.add(new Breadcrumb("#", message, messageEN, LAST_LINK_CLASS));
        }

        return breadcrumbs;
    }

    public List<Breadcrumb> getBreadcrumbs(final String value, final String valueEN) {
        final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
        if (StringUtils.isNotEmpty(value)) {
            breadcrumbs.add(new Breadcrumb("#", value, valueEN, LAST_LINK_CLASS));
        }

        return breadcrumbs;
    }

    public List<Breadcrumb> getBreadcrumbs(final String value) {
        final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
        if (StringUtils.isNotEmpty(value)) {
            breadcrumbs.add(new Breadcrumb("#", value, LAST_LINK_CLASS));
        }

        return breadcrumbs;
    }

    protected I18NService getI18nService() {
        return i18nService;
    }

    @Required
    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }

    protected MessageSource getMessageSource() {
        return messageSource;
    }

    @Required
    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}

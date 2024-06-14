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
package com.namics.distrelec.b2b.storefront.breadcrumb.impl;

import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;

/**
 * ManufacturerBreadcrumbBuilder implementation for display page title and manufacturer name
 */
public class ManufacturerBreadcrumbBuilder {
    private static final String LAST_LINK_CLASS = "active";
    private static final String MANUFACTURERS_CMS_URL = "/Manufacturers/cms/manufacturer";
    private static final String MANUFACTURER = "Manufacturer";

    private I18NService i18nService;
    private MessageSource messageSource;

    public List<Breadcrumb> getBreadcrumbs(final String value, final String uri) {
        final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
        if (StringUtils.isNotEmpty(value)) {
            breadcrumbs.add(getSimpleBreadcrumb(uri != null ? uri : "#", value));
        } else {
            breadcrumbs.add(getSimpleBreadcrumb("#", MANUFACTURER));
        }
        return breadcrumbs;
    }

    public List<Breadcrumb> getBreadcrumbs(final String value, final String url, final String name) {
        final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
        if (StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(name)) {
            breadcrumbs.add(getSimpleBreadcrumb(MANUFACTURERS_CMS_URL, StringUtils.isNotEmpty(value) ? value : MANUFACTURER));
            breadcrumbs.add(new Breadcrumb(url, name, LAST_LINK_CLASS));
        }
        return breadcrumbs;
    }

    public List<Breadcrumb> getBreadcrumbs(final String value, final String parentUrl, final String url, final String name) {
        final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
        if (StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(name)) {
            breadcrumbs.add(getSimpleBreadcrumb(StringUtils.isNotEmpty(parentUrl) ? parentUrl : MANUFACTURERS_CMS_URL, //
                    StringUtils.isNotEmpty(value) ? value : MANUFACTURER));
            breadcrumbs.add(new Breadcrumb(url, name, LAST_LINK_CLASS));
        }
        return breadcrumbs;
    }

    private Breadcrumb getSimpleBreadcrumb(final String url, final String value) {
        return new Breadcrumb(url, value, LAST_LINK_CLASS);
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

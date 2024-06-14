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
import java.util.Locale;

/**
 * AccountBreadcrumbBuilder implementation for account related pages.
 */
public class AccountBreadcrumbBuilder {
    private static final String ACCOUNT_URL = "/my-account";
    private static final String ACCOUNT_TITLE_MESSAGE_KEY = "text.account.accountDetails";

    private MessageSource messageSource;
    private I18NService i18nService;

    public List<Breadcrumb> getBreadcrumbs(final String navNodeKey, final String resourceKey) {
        final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
       String enTitle= getMessageSource().getMessage(navNodeKey, null, ACCOUNT_TITLE_MESSAGE_KEY,
                Locale.ENGLISH);
        breadcrumbs.add(new Breadcrumb(ACCOUNT_URL, getMessageSource().getMessage(navNodeKey, null, ACCOUNT_TITLE_MESSAGE_KEY,
                getI18nService().getCurrentLocale()),enTitle, null));

        if (StringUtils.isNotBlank(resourceKey)) {
        	String resourceENName= getMessageSource().getMessage(resourceKey, null, Locale.ENGLISH);
            breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage(resourceKey, null, getI18nService().getCurrentLocale()),resourceENName, null));
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

/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.breadcrumb.impl;

import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;

/**
 * ShoppingListBreadcrumbBuilder
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class ShoppingListBreadcrumbBuilder {

    private static final String LAST_LINK_CLASS = "active";
    private static final String LISTS_KEY = "shoppingListPage.breadcrumb.lists";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18nService;

    public List<Breadcrumb> getBreadcrumbs(final String listName) {
        final List<Breadcrumb> breadcrumbs = new ArrayList<>();
        final String lists = messageSource.getMessage(LISTS_KEY, null, "Lists", i18nService.getCurrentLocale());
        breadcrumbs.add(new Breadcrumb("#", lists, null));
        if (StringUtils.isNotEmpty(listName)) {
            breadcrumbs.add(new Breadcrumb("#", listName, LAST_LINK_CLASS));
        }
        return breadcrumbs;
    }
}

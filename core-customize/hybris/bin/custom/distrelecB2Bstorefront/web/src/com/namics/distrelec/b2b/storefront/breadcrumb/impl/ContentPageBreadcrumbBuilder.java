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

import com.namics.distrelec.b2b.storefront.tags.Functions;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.core.model.ItemModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Breadcrumb builder that uses page title in breadcrumb or page name as fallback when title is missing.
 */
public class ContentPageBreadcrumbBuilder {

    private static final String LAST_LINK_CLASS = "active";

    public List<Breadcrumb> getBreadcrumbs(final ContentPageModel page, final HttpServletRequest httpRequest) {
        List<Breadcrumb> breadcrumb = new ArrayList<Breadcrumb>();

        final List<CMSNavigationNodeModel> nodes = page.getNavigationNodeList();
        if (CollectionUtils.isNotEmpty(nodes)) {
            CMSNavigationNodeModel pageNode = nodes.get(0) == null ? null : nodes.get(0).getParent();
            while (pageNode != null && pageNode.getParent() != null && pageNode.getParent().getParent() != null) {
                final List<CMSNavigationEntryModel> entries = pageNode.getEntries();
                if (CollectionUtils.isNotEmpty(entries)) {
                    final ItemModel item = entries.get(0).getItem();
                    String breadcrumbTitle = null;
                    String breadcrumbTitleEN = null;
                    String breadcrumbLink = null;
                    if (item instanceof CMSLinkComponentModel) {
                        breadcrumbTitle = ((CMSLinkComponentModel) item).getLinkName();
                        breadcrumbTitleEN = ((CMSLinkComponentModel) item).getLinkName(Locale.ENGLISH);
                        breadcrumbLink = Functions.getUrlForCMSLinkComponent((CMSLinkComponentModel) item, httpRequest);
                    } else if (item instanceof CategoryModel) {
                        breadcrumbTitle = ((CategoryModel) item).getName();
                        breadcrumbTitleEN = ((CategoryModel) item).getName(Locale.ENGLISH);
                        breadcrumbLink = Functions.getUrlForCategory((CategoryModel) item, httpRequest);
                    } else if (item instanceof ContentPageModel) {
                        breadcrumbTitle = ((ContentPageModel) item).getTitle();
                        breadcrumbTitleEN = ((ContentPageModel) item).getTitle(Locale.ENGLISH);
                        breadcrumbLink = Functions.getUrlForContentPage((ContentPageModel) item, httpRequest);
                    }

                    if (StringUtils.isNotBlank(breadcrumbTitle) && StringUtils.isNotBlank(breadcrumbLink)) {
                        breadcrumb.add(new Breadcrumb(breadcrumbLink, breadcrumbTitle, breadcrumbTitleEN, ""));
                    }
                } else {
                    breadcrumb.add(new Breadcrumb(null, pageNode.getTitle(), pageNode.getTitle(Locale.ENGLISH), ""));
                }

                pageNode = pageNode.getParent();
            }
        }

        Collections.reverse(breadcrumb);
        if (breadcrumb.size() > 2) {
            breadcrumb = breadcrumb.subList(0, 1);
        }

        String title = page.getTitle();
        String titleEN = page.getTitle(Locale.ENGLISH);
        if (title == null) {
            title = page.getName();
            titleEN = page.getTitle(Locale.ENGLISH);
        }
        breadcrumb.add(new Breadcrumb("#", title, titleEN, LAST_LINK_CLASS));
        return breadcrumb;
    }
}

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
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * StorefinderBreadcrumbBuilder implementation for store finder related pages
 */
public class StorefinderBreadcrumbBuilder {
    public static final String LOCATION_QUERY_KEY = "locationQuery";
    public static final String STORE_LOCATION_KEY = "storeLocation";

    public List<Breadcrumb> getBreadcrumbs(final Map<String, String> itemMap) {
        final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();

        if (itemMap != null) {
            if (itemMap.containsKey(LOCATION_QUERY_KEY) && StringUtils.isNotBlank(itemMap.get(LOCATION_QUERY_KEY))) {
                breadcrumbs.add(new Breadcrumb("/store-finder?q=" + StringEscapeUtils.escapeHtml(itemMap.get(LOCATION_QUERY_KEY)), StringEscapeUtils
                        .escapeHtml(itemMap.get(LOCATION_QUERY_KEY)), null));
            }
            if (itemMap.containsKey(STORE_LOCATION_KEY) && StringUtils.isNotBlank(itemMap.get(STORE_LOCATION_KEY))) {
                breadcrumbs.add(new Breadcrumb("/store-finder/" + StringEscapeUtils.escapeHtml(itemMap.get(LOCATION_QUERY_KEY)) + "/"
                        + StringEscapeUtils.escapeHtml(itemMap.get(STORE_LOCATION_KEY)), StringEscapeUtils.escapeHtml(itemMap.get(STORE_LOCATION_KEY)), null));
            }
        }

        return breadcrumbs;
    }
}

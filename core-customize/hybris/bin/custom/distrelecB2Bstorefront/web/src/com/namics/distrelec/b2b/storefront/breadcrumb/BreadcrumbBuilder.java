/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.storefront.breadcrumb;

import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import de.hybris.platform.core.model.ItemModel;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * {@code BreadcrumbBuilder}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.18
 */
public interface BreadcrumbBuilder<T extends ItemModel> {

    /**
     * Build the breadcrumb for the specified item.
     * 
     * @param item
     *            the target item
     * @param httpRequest
     *            the HTTP Request object
     * @return a list of {@link Breadcrumb}
     */
    public List<Breadcrumb> getBreadcrumbs(final T item, final HttpServletRequest httpRequest);

}

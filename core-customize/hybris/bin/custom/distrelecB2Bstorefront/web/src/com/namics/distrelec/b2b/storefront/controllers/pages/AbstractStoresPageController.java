/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

/**
 * Controller for manufacturer stores page.
 */
@Controller
public abstract class AbstractStoresPageController extends AbstractSearchPageController {

    protected static final String FF_LOG_NAME = "navigation";

    protected abstract String getStoreCmsPageName();

    protected abstract List<Breadcrumb> getBreadcrumbs(final String pageTitle, final String uri);

    protected abstract FactFinderProductSearchPageData<SearchStateData, ProductData> getProducts(boolean tracking, String searchString,
            final SearchStateData searchState, final PageableData pageableData, final boolean generateASN);

    protected abstract void storeMetaKeywordsInModel(Model model, Object item);

}

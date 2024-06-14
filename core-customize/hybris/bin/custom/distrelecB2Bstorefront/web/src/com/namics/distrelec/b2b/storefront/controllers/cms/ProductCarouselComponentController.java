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
package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.product.ProductModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Controller for CMS ProductReferencesComponent.
 */
@Controller("ProductCarouselComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.ProductCarouselComponent)
public class ProductCarouselComponentController extends AbstractDistCMSComponentController<ProductCarouselComponentModel> {

    protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.DIST_MANUFACTURER);

    @Autowired
    @Qualifier("productFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    @Qualifier("productSearchFacade")
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Autowired
    @Qualifier("webtrekkFacade")
    private DistWebtrekkFacade distWebtrekkFacade;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final ProductCarouselComponentModel component) {
        final List<ProductData> products = new ArrayList<ProductData>();

        products.addAll(collectLinkedProducts(model, component));
        products.addAll(collectSearchProducts(model, component));

        model.addAttribute("title", component.getTitle());
        model.addAttribute("productData", products);
    }

    protected List<ProductData> collectLinkedProducts(final Model model, final ProductCarouselComponentModel component) {
        final List<ProductData> products = new ArrayList<ProductData>();

        for (final ProductModel productModel : component.getProducts()) {
            products.add(productFacade.getProductForCodeAndOptions(productModel.getCode(), PRODUCT_OPTIONS));
        }

        for (final CategoryModel categoryModel : component.getCategories()) {
            for (final ProductModel productModel : categoryModel.getProducts()) {
                products.add(productFacade.getProductForCodeAndOptions(productModel.getCode(), PRODUCT_OPTIONS));
            }
        }

        if (CollectionUtils.isNotEmpty(products)) {
            distWebtrekkFacade.addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
        }

        return products;
    }

    protected List<ProductData> collectSearchProducts(final Model model, final ProductCarouselComponentModel component) {
        final String searchQuery = component.getSearchQuery();
        final String categoryCode = component.getCategoryCode();
        List<ProductData> products = new ArrayList<ProductData>();

        if (searchQuery != null && categoryCode != null) {
            DistSearchType searchType = DistSearchType.CATEGORY;
            final SearchStateData searchState = new SearchStateData();
            final SearchQueryData searchQueryData = new SearchQueryData();
            searchQueryData.setValue(searchQuery);
            searchState.setQuery(searchQueryData);

            if (searchQuery.contains("q") && !searchQuery.contains("q=*")) {
                searchType = DistSearchType.CATEGORY_AND_TEXT;
            }

            final PageableData pageableData = new PageableData();
            pageableData.setPageSize(100); // Limit to 100 matching results

            final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = productSearchFacade.search(false, searchState, pageableData,
                    searchType, categoryCode, false, "internal", MapUtils.EMPTY_MAP);

            // DISTRELEC-5176: add filterstrings
            getProductSearchFacade().addFilterstrings(searchPageData);

            products = searchPageData.getResults();
        }

        if (CollectionUtils.isNotEmpty(products)) {
            distWebtrekkFacade.addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_FAF_ONSITE);
            return products;
        }

        return Collections.EMPTY_LIST;
    }

    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    public void setProductFacade(DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    public DistWebtrekkFacade getDistWebtrekkFacade() {
        return distWebtrekkFacade;
    }

    public void setDistWebtrekkFacade(DistWebtrekkFacade distWebtrekkFacade) {
        this.distWebtrekkFacade = distWebtrekkFacade;
    }

    public ProductSearchFacade<ProductData> getProductSearchFacade() {
        return productSearchFacade;
    }

    public void setProductSearchFacade(final ProductSearchFacade<ProductData> productSearchFacade) {
        this.productSearchFacade = productSearchFacade;
    }
}

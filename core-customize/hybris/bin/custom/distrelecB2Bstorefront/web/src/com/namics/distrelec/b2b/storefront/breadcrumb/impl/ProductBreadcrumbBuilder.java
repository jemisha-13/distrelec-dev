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

import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.storefront.history.BrowseHistory;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.variants.model.VariantProductModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * ProductBreadcrumbBuilder implementation for {@link ProductData}
 */
/*
 * Erik Fries 2016-02-29 DISTRELEC-8866 PDP (Product Detail Page) Redesign Removed final bread crumb (the product itself) Added link class
 * "filter" Changed lines marked with "//-"
 */

public class ProductBreadcrumbBuilder {

    private static final Logger LOG = LogManager.getLogger(ProductBreadcrumbBuilder.class);

    // - private static final String LAST_LINK_CLASS = "active";
    private static final String RETURN_TO_SEARCH_LINK_CLASS = "return";
    private static final String CUSTOM_FILTER_KEY = "breadcrumb.customFilter";
    private static final String FALL_BACK_STRING = "Custom Filter";
    private static final String HTTP_REFERER_HEADER = "Referer";

    private DistUrlResolver<ProductModel> productModelUrlResolver;
    private UrlResolver<ProductModel> catalogPlusProductModelUrlResolver;
    private DistUrlResolver<CategoryModel> categoryModelUrlResolver;
    private DistCategoryService categoryService;
    private BrowseHistory browseHistory;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18nService;

    public List<Breadcrumb> getBreadcrumbs(final ProductModel productModel, final String customFilterURL, final HttpServletRequest request) {
        final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();

        // DISTRELEC-11753
        final String referer = request.getHeader(HTTP_REFERER_HEADER);
        CategoryModel refCategory = null;
        if (StringUtils.contains(referer, "/c/cat-")) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Product Page coming from category Page");
            }

            try {
                final URL url = new URL(referer);
                final String catCode = StringUtils.substring(url.getPath(), StringUtils.indexOf(url.getPath(), "/c/cat-") + 3);
                // Check if the referer category is one of the super categories of the target product
                for (final CategoryModel supCat : productModel.getSupercategories()) {
                    if (StringUtils.equals(catCode, supCat.getCode())) {
                        refCategory = supCat;
                        break;
                    }
                    for (final CategoryModel catSup : supCat.getSupercategories()) {
                        if (StringUtils.equals(catCode, catSup.getCode())) {
                            refCategory = catSup;
                            break;
                        }
                    }
                    if (refCategory != null) {
                        break;
                    }
                }
            } catch (final MalformedURLException e) {
                LOG.error("Error occur while processing the Referer HTTP header");
            } catch (final SystemException e) {
                LOG.error("Error occur while fetching category mentioned in the Referer HTTP header");
            }
        }

        // - final Breadcrumb last;

        final ProductModel baseProductModel = getBaseProduct(productModel);

        final CategoryModel refererOrPrimaryCategory = refCategory != null ? refCategory : baseProductModel.getPrimarySuperCategory();

        // - last = getProductBreadcrumb(baseProductModel);
        Collection<CategoryModel> categoryModels = CollectionUtils.select( //
                refererOrPrimaryCategory != null ? Collections.singleton(refererOrPrimaryCategory) : baseProductModel.getSupercategories(),
                entry -> !(entry instanceof ClassificationClassModel));

        // - last.setLinkClass(LAST_LINK_CLASS);

        // - breadcrumbs.add(last);

        if (StringUtils.contains(customFilterURL, "?q=")) {
            // Adding CustomURL if the referer contains a query (facet) in the URL.
            breadcrumbs.add(getCustomFilterBreadcrumb(customFilterURL));
        }

        Collection<CategoryModel> refererModels = null;
        if (categoryModels.size() >= 2 && customFilterURL != null) {
            refererModels = CollectionUtils.select(categoryModels,
                    entry -> customFilterURL.toLowerCase().contains(((CategoryModel) entry).getCode().toLowerCase()));
        }
        if (CollectionUtils.isNotEmpty(refererModels)) {
            categoryModels = refererModels;
        }

        while (CollectionUtils.isNotEmpty(categoryModels)) {
            CategoryModel toDisplay = null;
            for (final CategoryModel categoryModel : categoryModels) {
                if (!(categoryModel instanceof ClassificationClassModel)) {
                    if (toDisplay == null) {
                        toDisplay = categoryModel;
                    }
                    if (getBrowseHistory().findUrlInHistory(categoryModel.getCode()) != null) {
                        break;
                    }
                }
            }
            categoryModels.clear();
            if (toDisplay != null && toDisplay.getLevel() != null && toDisplay.getLevel().intValue() != 0) {
                breadcrumbs.add(getCategoryBreadcrumb(toDisplay));
                categoryModels.addAll(toDisplay.getSupercategories());
            }
        }
        Collections.reverse(breadcrumbs);
        return breadcrumbs;
    }

    protected Breadcrumb getCustomFilterBreadcrumb(final String customFilterURL) {
        final String customFilterName = getMessageSource().getMessage(CUSTOM_FILTER_KEY, null, FALL_BACK_STRING, getI18nService().getCurrentLocale());
        final String customFilterNameEN = getMessageSource().getMessage(CUSTOM_FILTER_KEY, null, FALL_BACK_STRING, Locale.ENGLISH);
        return new Breadcrumb(customFilterURL, customFilterName,customFilterNameEN, RETURN_TO_SEARCH_LINK_CLASS);
    }

    protected ProductModel getBaseProduct(final ProductModel product) {
        if (product instanceof VariantProductModel) {
            return getBaseProduct(((VariantProductModel) product).getBaseProduct());
        }
        return product;
    }

    protected Breadcrumb getProductBreadcrumb(final ProductModel product) {
        return new Breadcrumb(getProductUrlResolver(product).resolve(product), product.getName(), product.getName(Locale.ENGLISH), null);
    }

    /**
     * Return the product URL resolver for the specified product
     * 
     * @param productModel
     * @return the product URL resolver.
     */
    protected UrlResolver<ProductModel> getProductUrlResolver(final ProductModel productModel) {
        return productModel.getCatPlusSupplierAID() == null ? getProductModelUrlResolver() : getCatalogPlusProductModelUrlResolver();
    }

    protected Breadcrumb getCategoryBreadcrumb(final CategoryModel category) {
        return new Breadcrumb(getCategoryModelUrlResolver().resolve(category), category.getName(), category.getName(Locale.ENGLISH), null);
    }

    public DistUrlResolver<ProductModel> getProductModelUrlResolver() {
        return productModelUrlResolver;
    }

    @Required
    public void setProductModelUrlResolver(final DistUrlResolver<ProductModel> productModelUrlResolver) {
        this.productModelUrlResolver = productModelUrlResolver;
    }

    public UrlResolver<ProductModel> getCatalogPlusProductModelUrlResolver() {
        return catalogPlusProductModelUrlResolver;
    }

    @Required
    public void setCatalogPlusProductModelUrlResolver(final UrlResolver<ProductModel> catalogPlusProductModelUrlResolver) {
        this.catalogPlusProductModelUrlResolver = catalogPlusProductModelUrlResolver;
    }

    public BrowseHistory getBrowseHistory() {
        return browseHistory;
    }

    @Required
    public void setBrowseHistory(final BrowseHistory browseHistory) {
        this.browseHistory = browseHistory;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public I18NService getI18nService() {
        return i18nService;
    }

    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }

    public DistCategoryService getCategoryService() {
        return categoryService;
    }

    public void setCategoryService(final DistCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Required
    public DistUrlResolver<CategoryModel> getCategoryModelUrlResolver() {
        return categoryModelUrlResolver;
    }

    public void setCategoryModelUrlResolver(final DistUrlResolver<CategoryModel> categoryModelUrlResolver) {
        this.categoryModelUrlResolver = categoryModelUrlResolver;
    }
}

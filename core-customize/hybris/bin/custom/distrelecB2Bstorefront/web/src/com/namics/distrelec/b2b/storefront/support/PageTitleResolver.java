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
package com.namics.distrelec.b2b.storefront.support;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.hybris.ffsearch.data.facet.FilterBadgeData;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Resolves page title according to page, search text, current category or product.
 */
public class PageTitleResolver {
    protected static final String PRODUCT_PAGE_TITLE_BUY_PROPERTY_KEY = "product.page.title.buy";
    protected static final String PAGE_TITLE_ONLINE_SHOP_PROPERTY_KEY = "page.title.online.shop";
    protected static final String REGEX_TO_STRIP_CHARACTERS = "[-._]";
    protected static final int MAX_TITLE_LENGTH = 60;
    public static final String LONG_SEPARATOR = " - ";
    protected static final String SHORT_SEPARATOR = " ";
    protected static final char CH_SPACE = ' ';
    private ProductService productService;
    private CommerceCategoryService commerceCategoryService;
    private CMSSiteService cmsSiteService;
    private MessageSource messageSource;
    private I18NService i18nService;

    private static final Logger LOG = LogManager.getLogger(PageTitleResolver.class);

    protected CommerceCategoryService getCommerceCategoryService() {
        return commerceCategoryService;
    }

    @Required
    public void setCommerceCategoryService(final CommerceCategoryService commerceCategoryService) {
        this.commerceCategoryService = commerceCategoryService;
    }

    protected ProductService getProductService() {
        return productService;
    }

    @Required
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    protected CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    @Required
    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public String resolveContentPageTitle(final String title) {
        final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();

        final StringBuilder builder = new StringBuilder();
        if (!StringUtils.isEmpty(title)) {
            builder.append(title).append(LONG_SEPARATOR);
        }
        builder.append(currentSite.getName());
        return StringEscapeUtils.escapeHtml(builder.toString());
    }

    public String resolveHomePageTitle(final String title) {
        final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
        final StringBuilder builder = new StringBuilder();
        builder.append(currentSite.getName());

        if (!StringUtils.isEmpty(title)) {
            builder.append(LONG_SEPARATOR).append(title);
        }

        return StringEscapeUtils.escapeHtml(builder.toString());
    }

    public <STATE> String resolveSearchPageTitle(final String searchText, final List<BreadcrumbData<STATE>> appliedFacets) {
        final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();

        final StringBuilder builder = new StringBuilder();
        if (!StringUtils.isEmpty(searchText)) {
            builder.append(searchText).append(LONG_SEPARATOR);
        }
        for (final BreadcrumbData pathElement : appliedFacets) {
            builder.append(pathElement.getFacetValueName()).append(LONG_SEPARATOR);
        }
        builder.append(currentSite.getName());
        return StringEscapeUtils.escapeHtml(builder.toString());
    }

    /**
     * creates page title for given code and facets
     */
    public <STATE> String resolveCategoryPageTitle(final CategoryModel category, final List<FilterBadgeData<STATE>> list) {
        final String name = (category != null) ? category.getName() : StringUtils.EMPTY;
        final StringBuilder builder = new StringBuilder();

        final boolean root_cat = (category == null || (category.getLevel() <= 1));
        final String titleMessageText = getMessageSource().getMessage(root_cat ? PAGE_TITLE_ONLINE_SHOP_PROPERTY_KEY : PRODUCT_PAGE_TITLE_BUY_PROPERTY_KEY, new Object[] { name },
                root_cat ? "Online Shop" : "Buy", getI18nService().getCurrentLocale());

        if (root_cat) {
            builder.append(name).append(CH_SPACE);
        }
        builder.append(titleMessageText);
        
        // Add shop Data
        appendShopNameSuffix(builder);

        return StringEscapeUtils.escapeHtml(builder.toString());
    }

    public String resolveManufacturerPageTitle(final DistManufacturerModel manufacturer) {
        final StringBuilder builder = new StringBuilder();
        final String name = (manufacturer != null) ? manufacturer.getName() : StringUtils.EMPTY;
        if (!StringUtils.isEmpty(name)) {
            builder.append(name).append(CH_SPACE);
        }
        builder.append(getMessageSource().getMessage(PAGE_TITLE_ONLINE_SHOP_PROPERTY_KEY, null, "Online Shop", getI18nService().getCurrentLocale()));

        // Add shop Data
        appendShopNameSuffix(builder);

        return StringEscapeUtils.escapeHtml(builder.toString());
    }

    public String getShopNameSuffix() {
        return getLongSeparator() + getSiteNameOrEmpty();
    }

    /**
     * creates page title for given code and facets
     */
    public <STATE> String resolveCategoryPageTitle(final String categoryCode, final List<FilterBadgeData<STATE>> appliedFacets) {
        final CategoryModel category = getCommerceCategoryService().getCategoryForCode(categoryCode);
        return resolveCategoryPageTitle(category, appliedFacets);
    }

    /**
     * creates page title for given code
     */
    public String resolveProductPageTitle(final ProductModel product) {
        // get title portions
        final String buy = StringUtils.trimToEmpty(getMessageSource().getMessage(PRODUCT_PAGE_TITLE_BUY_PROPERTY_KEY, new Object[] {""}, getI18nService().getCurrentLocale()));
        final String productName = StringUtils.defaultString(product.getName(), product.getCode());
        final String manufacturerName = (product.getManufacturer() != null && StringUtils.isNotBlank(product.getManufacturer().getName()))
                ? product.getManufacturer().getName()
                : StringUtils.EMPTY;
        final String mpn = StringUtils.defaultString(product.getTypeName());
        final StringBuilder titleStringBuilder = new StringBuilder();

        final String separator = (buy.length() + productName.length() + manufacturerName.length() + mpn.length() <= getMaxTitleLength()
                - 3 * (getLongSeparator().length())) ? getLongSeparator() : getShortSeparator();
        
        // Add MPN
        if (StringUtils.isNotEmpty(mpn)) {
            titleStringBuilder.append(mpn).append(separator);
        }

        // Add buy
        if (buy.length() + productName.length() + manufacturerName.length() + mpn.length() <= getMaxTitleLength() - 3 * (getLongSeparator().length())) {
            titleStringBuilder.append(buy).append(" ");
        }

        // Add product data
        titleStringBuilder.append(productName);
        if (StringUtils.isNotEmpty(manufacturerName)) {
            titleStringBuilder.append(separator).append(manufacturerName);
        }

        // Add shop Data
        appendShopNameSuffix(titleStringBuilder);

        return StringEscapeUtils
                .escapeHtml(titleStringBuilder.toString().length() > getMaxTitleLength()
                        ? StringUtils.removePattern(titleStringBuilder.toString(), REGEX_TO_STRIP_CHARACTERS)
                        : titleStringBuilder.toString());
    }

    protected String getSiteNameOrEmpty() {
        return (getCmsSiteService().getCurrentSite() != null) ? getCmsSiteService().getCurrentSite().getName() : StringUtils.EMPTY;
    }

    protected StringBuilder appendShopNameSuffix(final StringBuilder titleStringBuilder) {
        final String siteName = getSiteNameOrEmpty();
        final String siteNameNoCountry = siteName.split(" ")[0];
        if (titleStringBuilder.length() + getLongSeparator().length() + siteName.length() <= getMaxTitleLength()) {
            titleStringBuilder.append(getLongSeparator()).append(siteName);
        } else {
            if (titleStringBuilder.length() + getLongSeparator().length() + siteNameNoCountry.length() <= getMaxTitleLength()) {
                titleStringBuilder.append(getLongSeparator()).append(siteNameNoCountry);
            }
        }
        return titleStringBuilder;
    }

    public String stripShopNameSuffix(final String title) {
        final String siteName = getSiteNameOrEmpty();
        final String siteNameNoCountry = siteName.split(" ")[0];
        return StringUtils.removeEnd(StringUtils.removeEnd(title, getLongSeparator() + siteName), getLongSeparator() + siteNameNoCountry);
    }

    public String resolveProductPageTitle(final String productCode) {
        // Lookup the product
        final ProductModel product = getProductService().getProductForCode(productCode);
        return resolveProductPageTitle(product);
    }

    protected List<CategoryModel> getCategoryPath(final ProductModel product) {
        final CategoryModel category = getPrimaryCategoryForProduct(product);
        if (category != null) {
            return getCategoryPath(category);
        }
        return Collections.emptyList();
    }

    protected List<CategoryModel> getCategoryPath(final CategoryModel category) {
        final Collection<List<CategoryModel>> paths = getCommerceCategoryService().getPathsForCategory(category);
        // Return first - there will always be at least 1
        final List<CategoryModel> cat2ret = paths.iterator().next();
        Collections.reverse(cat2ret);
        return cat2ret;
    }

    protected CategoryModel getPrimaryCategoryForProduct(final ProductModel product) {
        // Get the first super-category from the product that isn't a classification category
        for (final CategoryModel category : product.getSupercategories()) {
            if (!(category instanceof ClassificationClassModel)) {
                return category;
            }
        }
        return null;
    }

    public String getRegexToStripCharacters() {
        return REGEX_TO_STRIP_CHARACTERS;
    }

    public int getMaxTitleLength() {
        return MAX_TITLE_LENGTH;
    }

    public String getLongSeparator() {
        return LONG_SEPARATOR;
    }

    public String getShortSeparator() {
        return SHORT_SEPARATOR;
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
}

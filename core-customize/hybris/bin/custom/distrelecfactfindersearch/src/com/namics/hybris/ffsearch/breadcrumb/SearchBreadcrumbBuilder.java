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
package com.namics.hybris.ffsearch.breadcrumb;

import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetValueData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


/**
 * SearchBreadcrumbBuilder implementation for {@link de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData}
 */
public class SearchBreadcrumbBuilder {

    private static final String LAST_LINK_CLASS = "active";
    public static final String SEARCH_QUERY_PARAMETER_NAME = DistrelecfactfindersearchConstants.SEARCH_QUERY_PARAMETER_NAME;
    private UrlResolver<CategoryModel> categoryModelUrlResolver;
    private MessageSource messageSource;
    private I18NService i18nService;
    private CommerceCategoryService commerceCategoryService;

    private static final String MANUFACTURERS_CMS_URL = "/Manufacturers/cms/manufacturer";
    private static final String MANUFACTURER = "Manufacturer";
    private static final String SEARCH = "search";

    public List<Breadcrumb> getSearchBreadcrumbs(final String pageTitle, final String url, final String name) {
        final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
        final StringBuilder searchTermString;
        if (StringUtils.isNotEmpty(pageTitle)) {
            searchTermString = new StringBuilder(pageTitle);
        } else {
            searchTermString = new StringBuilder("Search");
        }
        searchTermString.append(": ").append(name);

        breadcrumbs.add(new Breadcrumb(url, searchTermString.toString(), SEARCH, LAST_LINK_CLASS));
        return breadcrumbs;
    }

    public List<Breadcrumb> getBreadcrumbs(final CategoryModel category) {
        final LinkedList<Breadcrumb> breadcrumbs = new LinkedList<Breadcrumb>();

        if (category != null) {
            // Create category hierarchy path for breadcrumb
            breadcrumbs.add(getCategoryBreadcrumb(category, LAST_LINK_CLASS));

            CategoryModel currentCategory = getSuperCategory(category);
            while (currentCategory != null && currentCategory.getLevel() != null && currentCategory.getLevel().intValue() != 0) {
                breadcrumbs.addFirst(getCategoryBreadcrumb(currentCategory));
                currentCategory = getSuperCategory(currentCategory);
            }
        }

        return breadcrumbs;
    }

    private CategoryModel getSuperCategory(final CategoryModel category) {
        for (final CategoryModel superCategory : category.getSupercategories()) {
            if (superCategory != null && !(superCategory instanceof ClassificationClassModel)) {
                return superCategory;
            }
        }

        return null;
    }

    protected Breadcrumb getCategoryBreadcrumb(final CategoryModel category) {
        return getCategoryBreadcrumb(category, null);
    }

    protected Breadcrumb getCategoryBreadcrumb(final CategoryModel category, final String linkClass) {
        return new Breadcrumb(getCategoryModelUrlResolver().resolve(category), category.getName(), category.getName(Locale.ENGLISH), linkClass);
    }

    // used to build breadCrumbs on search pages
    public List<Breadcrumb> getSearchBreadcrumbs(final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData) {
        final LinkedList<Breadcrumb> breadcrumbs = (LinkedList) getCommanBreadCrum(searchPageData);
        breadcrumbs.addFirst(new Breadcrumb(searchPageData.getRemoveFiltersURL(), "Search: " + searchPageData.getFreeTextSearch(), SEARCH, null));
        return breadcrumbs;
    }

    // used to build breadCrumbs on manufacturer pages
    public List<Breadcrumb> getManufacturerBreadcrumbs(final String value, final String parentUrl, final String url, final String name,
                                                       final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData) {

        final LinkedList<Breadcrumb> breadcrumbs = (LinkedList) getCommanBreadCrum(searchPageData);

        if (StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(name)) {
            breadcrumbs.addFirst(getSimpleBreadcrumb(StringUtils.isNotEmpty(url) ? url : MANUFACTURERS_CMS_URL, //
                    StringUtils.isNotEmpty(name) ? name : MANUFACTURER, StringUtils.isNotEmpty(name) ? name : MANUFACTURER));

            breadcrumbs.addFirst(getSimpleBreadcrumb(StringUtils.isNotEmpty(parentUrl) ? parentUrl : MANUFACTURERS_CMS_URL, //
                    StringUtils.isNotEmpty(value) ? value : MANUFACTURER, MANUFACTURER));

        }

        return breadcrumbs;
    }

    private List<Breadcrumb> getCommanBreadCrum(final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData) {
        final LinkedList<Breadcrumb> breadcrumbs = new LinkedList<Breadcrumb>();
        String lastCatName = "";
        List<FactFinderFacetValueData<SearchStateData>> fffvdList = new ArrayList<FactFinderFacetValueData<SearchStateData>>();
        if (searchPageData != null && searchPageData.getCategories() != null) {
            fffvdList = searchPageData.getCategories().getValues();
            for (final FactFinderFacetValueData<SearchStateData> fffvd : fffvdList) {
                if (fffvd.isSelected()) {
                    lastCatName = fffvd.getName();
                }
            }
        }

        if (StringUtils.isEmpty(lastCatName)) {
            return breadcrumbs;
        }

        final CategoryModel category = commerceCategoryService.getCategoryForCode(lastCatName);

        if (category != null) {
            // Create category hierarchy path for breadcrumb
            // breadcrumbs.add(getSearchBreadcrumb(fffvdList, category, LAST_LINK_CLASS));
            breadcrumbs.add(getCategoryBreadcrumb(category, LAST_LINK_CLASS));
            CategoryModel currentCategory = getSuperCategory(category);
            while (currentCategory != null && currentCategory.getLevel() != null && currentCategory.getLevel().intValue() != 0) {
                breadcrumbs.addFirst(getCategorySearchBreadcrumb(fffvdList, currentCategory));
                currentCategory = getSuperCategory(currentCategory);
            }
        }

        return breadcrumbs;
    }

    public List<Breadcrumb> getStoreBreadCrum(final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData, final String value,
                                              final String valueEN, final String uri) {
        final LinkedList<Breadcrumb> breadcrumbs = (LinkedList) getCommanBreadCrum(searchPageData);
        breadcrumbs.addFirst(getSimpleBreadcrumb(uri != null ? uri : "#", value, valueEN));
        return breadcrumbs;
    }

    public List<Breadcrumb> getGeneralBreadcrumbs(final String value, final String valueEN, final String uri) {
        final LinkedList<Breadcrumb> breadcrumbs = new LinkedList<Breadcrumb>();
        breadcrumbs.addFirst(getSimpleBreadcrumb(uri != null ? uri : "#", value, valueEN));
        return breadcrumbs;
    }

    // used to build breadCrumbs on category pages
    public List<Breadcrumb> getCategoryBreadcrumbs(final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData) {
        return getCommanBreadCrum(searchPageData);
    }

    private Breadcrumb getSimpleBreadcrumb(final String url, final String value, final String valueEN) {
        return new Breadcrumb(url, value, valueEN, LAST_LINK_CLASS);
    }

    protected Breadcrumb getSearchBreadcrumb(final List<FactFinderFacetValueData<SearchStateData>> fffvdList, final CategoryModel category) {
        return getSearchBreadcrumb(fffvdList, category, null);
    }

    protected Breadcrumb getSearchBreadcrumb(final List<FactFinderFacetValueData<SearchStateData>> fffvdList, final CategoryModel category,
                                             final String linkClass) {
        for (FactFinderFacetValueData<SearchStateData> fffvd : fffvdList) {
            if (null != fffvd.getName() && fffvd.getName().equalsIgnoreCase(category.getCode())) {
                final StringBuilder filterurl = new StringBuilder(fffvd.getQuery().getUrl());
                if (filterurl.indexOf(DistrelecfactfindersearchConstants.FILTER_CATEGORY_CODE_PATH_ROOT) != -1) {
                    filterurl.delete(filterurl.indexOf(DistrelecfactfindersearchConstants.FILTER_CATEGORY_CODE_PATH_ROOT) + 1, filterurl.length());

                }
                filterurl.append(removeFilterPromotionLebel(fffvd.getQueryFilter()));
                filterurl.append("&filter_").append(fffvd.getCode()).append("=").append(fffvd.getName());
                return new Breadcrumb(filterurl.toString(), category.getName(i18nService.getCurrentLocale()), category.getName(Locale.ENGLISH), linkClass);
            }
        }
        return null;
    }

    protected Breadcrumb getCategorySearchBreadcrumb(final List<FactFinderFacetValueData<SearchStateData>> fffvdList, final CategoryModel category) {
        return getSearchBreadcrumb(fffvdList, category, null);
    }

    protected Breadcrumb getCategorySearchBreadcrumb(final List<FactFinderFacetValueData<SearchStateData>> fffvdList, final CategoryModel category,
                                                     final String linkClass) {
        for (FactFinderFacetValueData<SearchStateData> fffvd : fffvdList) {
            if (null != fffvd.getName() && fffvd.getName().equalsIgnoreCase(category.getCode())) {
                final StringBuilder filterurl = new StringBuilder(fffvd.getQuery().getUrl());
                if (filterurl.indexOf(DistrelecfactfindersearchConstants.FILTER_CATEGORY_CODE_PATH_ROOT) != -1) {
                    filterurl.delete(filterurl.indexOf(DistrelecfactfindersearchConstants.FILTER_CATEGORY_CODE_PATH_ROOT) + 1, filterurl.length());

                }
                filterurl.append(removeFilterPromotionLebel(fffvd.getQueryFilter()));
                filterurl.append("&filter_").append(fffvd.getCode()).append("=").append(fffvd.getName());
                return new Breadcrumb(filterurl.toString(), category.getName(i18nService.getCurrentLocale()), category.getName(Locale.ENGLISH), linkClass);
            }
        }
        return null;
    }

    private StringBuilder removeFilterPromotionLebel(final String queryFilter) {
        final StringBuilder filterurl = new StringBuilder(queryFilter);
        if (filterurl.indexOf("&filter_PromotionLabels") != -1) {
            // System.out.println(" -- " + queryFilter);
            final String cut1 = queryFilter.substring(0, queryFilter.indexOf("filter_PromotionLabels"));
            String cut2 = queryFilter.substring(queryFilter.indexOf(cut1) + cut1.length());
            if (cut2.indexOf("&") != -1) {
                cut2 = cut2.substring(0, cut2.indexOf("&") + 1);
            }
            filterurl.delete(filterurl.indexOf(cut2), filterurl.indexOf(cut2) + cut2.length());
            // System.out.println(" -- " + filterurl);
        }
        return filterurl;
    }

    public UrlResolver<CategoryModel> getCategoryModelUrlResolver() {
        return categoryModelUrlResolver;
    }

    @Required
    public void setCategoryModelUrlResolver(final UrlResolver<CategoryModel> categoryModelUrlResolver) {
        this.categoryModelUrlResolver = categoryModelUrlResolver;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    @Required
    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public I18NService getI18nService() {
        return i18nService;
    }

    @Required
    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }

    public CommerceCategoryService getCommerceCategoryService() {
        return commerceCategoryService;
    }

    public void setCommerceCategoryService(CommerceCategoryService commerceCategoryService) {
        this.commerceCategoryService = commerceCategoryService;
    }

}

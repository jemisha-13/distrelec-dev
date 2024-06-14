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
package com.namics.distrelec.b2b.storefront.tags;

import com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService;
import com.namics.distrelec.b2b.core.service.customer.DistPunchoutService;
import com.namics.distrelec.b2b.core.service.url.ContentPageData;
import com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageController;
import com.namics.distrelec.b2b.storefront.servlets.util.FilterSpringUtil;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * JSP EL Functions. This file contains static methods that are used by JSP EL.
 */
public class Functions {
    /**
     * JSP EL Function to get a primary Image for a Product in a specific format
     * 
     * @param product
     *            the product
     * @param format
     *            the desired format
     * @return the image
     */
    public static ImageData getPrimaryImageForProductAndFormat(final ProductData product, final String format) {
        if (product != null && format != null) {
            final Collection<ImageData> images = product.getImages();
            if (images != null && !images.isEmpty()) {
                for (final ImageData image : images) {
                    if (ImageDataType.PRIMARY.equals(image.getImageType()) && format.equals(image.getFormat())) {
                        return image;
                    }
                }
            }
        }
        return null;
    }

    /**
     * JSP EL Function to get an Image for a Store in a specific format
     * 
     * @param store
     *            the store
     * @param format
     *            the desired image format
     * @return the image
     */
    public static ImageData getImageForStoreAndFormat(final PointOfServiceData store, final String format) {
        if (store != null && format != null) {
            final Collection<ImageData> images = store.getStoreImages();
            if (images != null && !images.isEmpty()) {
                for (final ImageData image : images) {
                    if (format.equals(image.getFormat())) {
                        return image;
                    }
                }
            }
        }
        return null;
    }

    /**
     * JSP EL Function to get the URL for a CMSLinkComponent
     * 
     * @param component
     *            The Link Component
     * @param httpRequest
     *            The current request (used to lookup spring beans)
     * @return The URL
     */
    public static String getUrlForCMSLinkComponent(final CMSLinkComponentModel component, final HttpServletRequest httpRequest) {
        return getUrlForCMSLinkComponent(component, httpRequest, null, null, null);
    }

    public static String getUrlForCMSLinkComponent(final CMSLinkComponentModel component, final HttpServletRequest httpRequest,
            final Converter<ProductModel, ProductData> productUrlConverter, final Converter<CategoryModel, CategoryData> categoryUrlConverter,
            final Converter<ContentPageModel, ContentPageData> contentPageUrlConverter) {
        // Try to get the URL from the component
        {
            final String url = component.getLocalizedUrl();
            if (url != null && !url.isEmpty()) {
                return url;
            }
        }

        // Try to get the label for the content page
        {
            final ContentPageModel contentPage = component.getContentPage();
            if (contentPage != null) {
                final Converter<ContentPageModel, ContentPageData> urlConverter = contentPageUrlConverter != null ? contentPageUrlConverter
                        : getContentPageUrlConverter(httpRequest);
                return urlConverter.convert(contentPage).getUrl();
            }
        }

        // Try to get the category and build a URL to the category
        final CategoryModel category = component.getCategory();
        if (category != null) {
            final Converter<CategoryModel, CategoryData> urlConverter = categoryUrlConverter != null ? categoryUrlConverter
                    : getCategoryUrlConverter(httpRequest);
            return urlConverter.convert(category).getUrl();
        }

        // Try to get the product and build a URL to the product
        final ProductModel product = component.getProduct();
        if (product != null) {
            final Converter<ProductModel, ProductData> urlConverter = productUrlConverter != null ? productUrlConverter : getProductUrlConverter(httpRequest);
            return urlConverter.convert(product).getUrl();
        }
        return null;
    }

    /**
     * JSP EL Function to get the URL for a Category
     * 
     * @param category
     *            The Category Model
     * @param httpRequest
     *            The current request (used to lookup spring beans)
     * @return The URL
     */
    public static String getUrlForCategory(final CategoryModel category, final HttpServletRequest httpRequest) {
        return getUrlForCategory(category, httpRequest, null);
    }

    public static String getUrlForCategory(final CategoryModel category, final HttpServletRequest httpRequest,
            final Converter<CategoryModel, CategoryData> categoryUrlConverter) {

        final Converter<CategoryModel, CategoryData> urlConverter = categoryUrlConverter != null ? categoryUrlConverter : getCategoryUrlConverter(httpRequest);
        return urlConverter.convert(category).getUrl();

    }

    /**
     * JSP EL Function to get the URL for a Content Page
     * 
     * @param contentPage
     *            The Content Page Model
     * @param httpRequest
     *            The current request (used to lookup spring beans)
     * @return The URL
     */
    public static String getUrlForContentPage(final ContentPageModel contentPage, final HttpServletRequest httpRequest) {
        return getUrlForContentPage(contentPage, httpRequest, null);
    }

    public static String getUrlForContentPage(final ContentPageModel contentPage, final HttpServletRequest httpRequest,
            final Converter<ContentPageModel, ContentPageData> contentPageUrlConverter) {

        final Converter<ContentPageModel, ContentPageData> urlConverter = contentPageUrlConverter != null ? contentPageUrlConverter
                : getContentPageUrlConverter(httpRequest);
        return urlConverter.convert(contentPage).getUrl();

    }

    /**
     * Utility method that encodes given URL
     * 
     * @param url
     * @return encoded URL
     */
    public static String encodeUrl(final String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            return url;
        }
    }

    /**
     * JSP EL Function to check if the navigation node has not only punched out categories
     * 
     * @param item
     *            The current navigation node
     * @param httpRequest
     *            The current request (used to lookup spring beans)
     * @return Boolean
     */
    public static Boolean hasNotOnlyPunchedOutCategories(final CMSNavigationNodeModel node, final HttpServletRequest httpRequest) {
        if (getDistPunchoutService(httpRequest).hasNotOnlyPunchedOutCategories(node)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * JSP EL Function to check if the category is punched out for the user session
     * 
     * @param category
     *            The category
     * @param httpRequest
     *            The current request (used to lookup spring beans)
     * @return Boolean
     */
    public static Boolean isCategoryPunchedout(final String categoryCode, final HttpServletRequest httpRequest) {
        if (getDistPunchoutService(httpRequest).isCategoryPunchedout(categoryCode)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * JSP EL Function to check if the catalog navigation node should be displayed
     * 
     * @param item
     *            The current navigation node
     * @param httpRequest
     *            The current request (used to lookup spring beans)
     * @return Boolean
     */
    public static Boolean displayCatalogEProcurement(final ItemModel item, final HttpServletRequest httpRequest) {
        if (item instanceof CategoryModel) {
            final List<CategoryModel> allowedCategories = getDistEProcurementCustomerConfigService(httpRequest).getAllowedCategories();
            if (CollectionUtils.isNotEmpty(allowedCategories) && !allowedCategories.contains(item)) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * Adds a parameter to a String representing an URL
     * 
     * @param url
     *            the URL in String format
     * @param keyToAdd
     *            the parameter key
     * @param valueToAdd
     *            the parameter value
     * @return the modified URL in String format
     */
    public static String addParameterToUrlString(final String url, final String keyToAdd, final String valueToAdd) {
        final NameValuePair parameterToAdd = new BasicNameValuePair(keyToAdd, valueToAdd);
        return addParameterToUrlString(url, parameterToAdd);
    }

    /**
     * Adds a parameter to a String representing an URL
     * 
     * @param url
     * @param parameterToAdd
     * @return a String
     */
    public static String addParameterToUrlString(final String url, final NameValuePair parameterToAdd) {
        final String fragment = StringUtils.substringAfter(url, "#");
        final String urlWithoutFragment = StringUtils.removeEnd(StringUtils.removeEnd(url, fragment), "#");
        final String beforeParameters = StringUtils.substringBefore(urlWithoutFragment, "?");
        // final String parameters = StringUtils.substringBetween(url, "?", "#");
        String parameters = StringUtils.removeStart(urlWithoutFragment, beforeParameters);
        parameters = StringUtils.removeStart(parameters, "?");
        final List<NameValuePair> parameterPairs = new ArrayList<>(URLEncodedUtils.parse(parameters, Charsets.UTF_8, '&'));
        parameterPairs.add(parameterToAdd);
        return beforeParameters + "?" + URLEncodedUtils.format(parameterPairs, '&', Charsets.UTF_8) + (StringUtils.contains(url, "#") ? "#" : "") + fragment;
    }

    /**
     * Adds the uid of the CMSItemModel as a query parameter to the URL
     * 
     * @param url
     *            the URL in String format
     * @param navigationSource
     *            the CMSItemModel to add to the URL
     * @return the modified URL in String format
     * @throws NullPointerException
     *             if any of the parameter is null
     */
    public static String addNavigationSourceToUrl(final String url, final CMSItemModel navigationSource) {
        Objects.requireNonNull(url);
        Objects.requireNonNull(navigationSource);
        return addParameterToUrlString(url, AbstractPageController.URL_PARAMETER_KEY_FOR_NAVIGATION_SOURCE, navigationSource.getUid());
    }

    protected static Converter<ProductModel, ProductData> getProductUrlConverter(final HttpServletRequest httpRequest) {
        return FilterSpringUtil.getSpringBean(httpRequest, "productUrlConverter", Converter.class);
    }

    protected static Converter<CategoryModel, CategoryData> getCategoryUrlConverter(final HttpServletRequest httpRequest) {
        return FilterSpringUtil.getSpringBean(httpRequest, "categoryUrlConverter", Converter.class);
    }

    protected static Converter<ContentPageModel, ContentPageData> getContentPageUrlConverter(final HttpServletRequest httpRequest) {
        return FilterSpringUtil.getSpringBean(httpRequest, "contentPageUrlConverter", Converter.class);
    }

    protected static DistEProcurementCustomerConfigService getDistEProcurementCustomerConfigService(final HttpServletRequest httpRequest) {
        return FilterSpringUtil.getSpringBean(httpRequest, "distEProcurementCustomerConfigService", DistEProcurementCustomerConfigService.class);
    }

    protected static SessionService getSessionService(final HttpServletRequest httpRequest) {
        return FilterSpringUtil.getSpringBean(httpRequest, "sessionService", SessionService.class);
    }

    protected static DistPunchoutService getDistPunchoutService(final HttpServletRequest httpRequest) {
        return FilterSpringUtil.getSpringBean(httpRequest, "distPunchoutService", DistPunchoutService.class);
    }
}

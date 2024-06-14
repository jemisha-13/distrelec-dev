/*
 * Copyright 2000-2017 Distrelec AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.message.queue.service;

import java.util.List;

import com.namics.distrelec.b2b.core.message.queue.dao.RelatedItemData;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * {@code InternalLinkMessageQueueService}
 * 
 *
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
public interface InternalLinkMessageQueueService {

    /**
     * Calculate the related products for the product given by its code.
     * 
     * @param productCode
     * @param site
     * @return a list of {@link ProductModel}
     */
    List<ProductModel> fetchRelatedProductsForProduct(final String productCode, final String site);

    /**
     * Calculate the related categories for the product given by its code.
     * 
     * @param productCode
     * @param site
     * @return a list of {@link CategoryModel}
     */
    List<CategoryModel> fetchRelatedCategoriesForProduct(final String productCode, final String site);

    /**
     * Calculate the related manufacturers for the product given by its code.
     * 
     * @param productCode
     * @param site
     * @return a list of {@link DistManufacturerModel}
     */
    List<DistManufacturerModel> fetchRelatedManufacturersForProduct(final String productCode, final String site,
            final List<CategoryModel> preSelectedCategories);

    /**
     * Fetch the related manufacturers for the category given by it's code.
     * 
     * @param categoryCode
     *            the target category code
     * @param site
     *            the target site UID.
     * @return the list of {@link RelatedItemData} related to the manufacturer and count.
     */
    List<RelatedItemData<DistManufacturerModel>> fetchRelatedManufacturersForCategory(final String categoryCode, final String site);

    /**
     * Fetch the related category for the given manufacturers list
     * 
     * @param manufacturers
     *            the previously selected manufacturers
     * @param site
     *            the target site UID.
     * @return the list of {@link CategoryModel} related to the category.
     */
    List<RelatedItemData<CategoryModel>> fetchRelatedCategoriesForCategory(List<DistManufacturerModel> manufacturers, final String site);

    /**
     * Fetch the related products for the category given by it's code.
     * 
     * @param categoryCode
     *            the target category code
     * @param site
     *            the target site UID.
     * @return the list of {@link ProductModel} related to the category.
     */
    List<RelatedItemData<ProductModel>> fetchRelatedProductsForCategory(final String categoryCode, final String site);

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11506 - Manufacturer point 1
     * </p>
     * <p>
     * fetches the categories which contain more stocked product of the given {@code manufacturer} that can be sold on {@code site}
     * </p>
     * 
     * @param manufacturer
     *            the manufacturer
     * @param site
     *            the site where the products can be sold
     * @return a list, sorted in descending order, of the categories with most stocked products
     */
    List<CategoryModel> fetchRelatedCategoriesForManufacturer(final DistManufacturerModel manufacturer, final CMSSiteModel site);
    
    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11506 Manufacturer point 2
     * </p>
     * <p>
     * fetches check the top 5 categories of this manufacturer (the category with the largest amount of in stock products of this
     * manufacturer), take the top 5 manufacturer of this 5 categories. If the top 5 categories are already known, use
     * {@code com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService.fetchRelatedManufacturers(List<CategoryModel>, CMSSiteModel, List<DistManufacturerModel>)}
     * for better performance
     * </p>
     * 
     * @param manufacturer
     *            the manufacturer
     * @param site
     *            the site where the products can be sold
     * @return a list, sorted in descending order, of the Manufacturers more related to given {@code manufacturer}
     */
    @Deprecated
    List<DistManufacturerModel> fetchRelatedManufacturers(DistManufacturerModel manufacturer, CMSSiteModel site);

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11506 Manufacturer point 2
     * </p>
     * <p>
     * given the top 5 categories of this manufacturer (the category with the largest amount of in stock products of this manufacturer),
     * take the top 5 manufacturer of this 5 categories
     * </p>
     * 
     * @param manufacturer
     *            the manufacturer
     * @param site
     *            the site where the products can be sold
     * @param categories
     *            the top 5 categories of this manufacturer, this is the output of
     *            {@code com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService.fetchRelatedCategoriesForManufacturer(DistManufacturerModel,
     *            CMSSiteModel)}
     * 
     * @return a list, sorted in descending order, of the Manufacturers more related to given {@code manufacturer}
     */
    List<DistManufacturerModel> fetchRelatedManufacturers(final DistManufacturerModel manufacturer, final CMSSiteModel site,
            final List<CategoryModel> categories);

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11506 Manufacturer point 3
     * </p>
     * fetches the products with the latest "SalesOrg specific product attributes" (with cut-off at 12 months)"
     * </p>
     * 
     * @param manufacturerModel
     *            the target manufacturer
     * @param siteModel
     *            The website for which we want to know the new Arrivals
     * @return a list of ProductModels sorted by "SalesOrg specific product attributes" creation date
     */
    List<ProductModel> fetchNewArrivalsOfManufacturer(DistManufacturerModel manufacturerModel, CMSSiteModel siteModel);
    
    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-12853 - Manufacturer point 4
     * </p>
     * <p>
     * check the top 5 most ((stocked+1) * (most bought+1)) products of the manufacturer selected
     * </p>
     * 
     * @param manufacturerModel
     *            the target Manufacturer
     * @param siteModel
     *            The website for which we want to know the TopSellers
     * @return a list of ProductModels sorted by score
     */
    List<ProductModel> fetchTopSellersOfManufacturer(DistManufacturerModel manufacturerModel, CMSSiteModel siteModel);

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11506 Manufacturer point 3
     * </p>
     * fetches the products with the latest "SalesOrg specific product attributes" (with cut-off at 12 months)"
     * </p>
     * 
     * @param categoryModel
     *            the target category
     * @param siteModel
     *            The website for which we want to know the new Arrivals
     * @return a list of ProductModels sorted by "SalesOrg specific product attributes" creation date
     */
    List<ProductModel> fetchNewArrivalsOfCategory(final CategoryModel categoryModel, final CMSSiteModel siteModel);

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-12852 - Categories point 4
     * </p>
     * <p>
     * check the top 5 most ((stocked+1) * (most bought+1)) item in any of the categories selected.
     * </p>
     * 
     * @param categoryModel
     *            the target Category
     * @param siteModel
     *            The website for which we want to know the TopSellers
     * @return a list of ProductModels sorted by score
     */
    List<RelatedItemData<ProductModel>> fetchTopSellersOfCategory(CategoryModel categoryModel, CMSSiteModel siteModel);

}

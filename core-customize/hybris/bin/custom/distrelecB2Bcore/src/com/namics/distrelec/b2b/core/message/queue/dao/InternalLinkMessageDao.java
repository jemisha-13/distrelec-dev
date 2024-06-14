/*
 * Copyright 2000-2017 Distrelec AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.message.queue.dao;

import java.util.Collection;
import java.util.List;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

/**
 * {@code InternalLinkMessageDao}
 * 
 *
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @since Distrelec 6.1
 */
public interface InternalLinkMessageDao extends Dao {

    List<ProductModel> fetchRelatedProductsForProduct(final ProductModel product, final CMSSiteModel site);

    List<CategoryModel> fetchRelatedCategoriesForProduct(final ProductModel product, final CMSSiteModel site);

    List<DistManufacturerModel> fetchRelatedManufacturersForProduct(final ProductModel product, final CMSSiteModel site);

    List<DistManufacturerModel> fetchRelatedManufacturersForProduct(final ProductModel product, final CMSSiteModel site,
            final List<CategoryModel> preSelectedCategories);

    List<RelatedItemData<DistManufacturerModel>> fetchRelatedManufacturersForCategory(final Collection<CategoryModel> categories, final CMSSiteModel site);

    List<RelatedItemData<ProductModel>> fetchRelatedProductsForCategory(final Collection<CategoryModel> categories, final CMSSiteModel site);

    List<RelatedItemData<CategoryModel>> fetchRelatedCategoriesForCategory(final List<DistManufacturerModel> manufacturers, final CMSSiteModel site);

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
     * given the top 5 categories of this manufacturer (the category with the largest amount of in stock products of this manufacturer),
     * take the top 5 manufacturer of this 5 categories
     * </p>
     * 
     * @param categories
     *            the top 5 categories of this manufacturer
     * @param site
     *            the site where the products can be sold
     * @return a list, sorted in descending order, of the Manufacturers more related to given {@code manufacturer}
     */
    List<DistManufacturerModel> fetchRelatedManufacturers(final List<CategoryModel> categories, final CMSSiteModel site,
            final List<DistManufacturerModel> excludedManifacturers);

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11506 Manufacturer point 3
     * </p>
     * fetches the products with the latest "SalesOrg specific product attributes" (with cut-off at 12 months)"
     * </p>
     * 
     * @param categories
     *            the target categories
     * @param siteModel
     *            The website for which we want to know the new Arrivals
     * @return a list of ProductModels sorted by "SalesOrg specific product attributes" creation date
     */
    List<ProductModel> fetchNewArrivalsOfCategories(final Collection<CategoryModel> categories, final CMSSiteModel siteModel);

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
    List<ProductModel> fetchNewArrivalsOfManufacturer(final DistManufacturerModel manufacturerModel, final CMSSiteModel siteModel);

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
    List<ProductModel> fetchTopSellersOfManufacturer(final DistManufacturerModel manufacturerModel, final CMSSiteModel siteModel);

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-12852 - Categories point 4
     * </p>
     * <p>
     * check the top 5 most ((stocked+1) * (most bought+1)) item in any of the categories selected.
     * </p>
     * 
     * @param categories
     *            the returned products will belong to any of these categories
     * @param siteModel
     *            The website for which we want to know the TopSellers
     * @return a list of ProductModels sorted by score
     */
    List<RelatedItemData<ProductModel>> fetchTopSellersInCategories(Collection<CategoryModel> categories, CMSSiteModel siteModel);
}

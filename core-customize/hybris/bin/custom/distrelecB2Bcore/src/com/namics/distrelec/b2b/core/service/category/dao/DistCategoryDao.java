/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.category.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.namics.distrelec.b2b.core.model.CategoryCountryModel;

import de.hybris.platform.category.daos.CategoryDao;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;

/**
 * Interface for category DAO.
 * 
 * @author pnueesch, Namics AG
 * @since Namics Extensions 1.0
 */
public interface DistCategoryDao extends CategoryDao {

    /**
     * Finds country specific category information by a category and a country.
     * 
     * @param category
     * @param country
     * @return DistManufacturerCountryModel
     */
    CategoryCountryModel findCountrySpecificCategoryInformation(final CategoryModel category, final CountryModel country);

    /**
     * Load all categories
     * 
     * @return a list of {@code CategoryModel}
     */
    @Deprecated
    List<CategoryModel> getAll();

    List<CategoryModel> findEmptyCategories();

    /**
     * Retrieve all categories with level greater than {@code from} (inclusive) and less than {@code to} (exclusive)
     * 
     * @return a list of {@code CategoryModel}
     */
    List<CategoryModel> getCategoryByLevelRange(final int from, final int to);

    /**
     * Check whether there is any category having a predecessor the category given by its code. If multiple successors found, then the one
     * with the latest predecessor time-stamp will be returned.
     * 
     * @param code
     *            the predecessor category code
     * @return the successor, if any, of the category given by its code. {@code null} of the category has no successor.
     */
    CategoryModel findSuccessor(final String code);

    /**
     * Check whether the category has at least one product or sub-category.
     * 
     * <b>Note</b> This method does not check the sales status nor punchouts.
     * 
     * @param category
     * @return {@code true} if the category has at least one product or sub-category, {@code false} otherwise.
     */
    boolean hasProductsOrSubCategories(final CategoryModel category);

    boolean hasMultipleProductsInFamily(final String code);

    List<CategoryModel> findCategoriesByCodes(final List<String> codes);

    Optional<CategoryModel> findProductFamily(String code);

    boolean categoryHasVisibleProduct(CategoryModel category, CMSSiteModel cmsSite);

    /**
     * Get all visible categories for a site.
     *
     * Used in longer jobs to avoid repetitive calling of categoryHasVisibleProduct which can be repetitive and overlapping.
     *
     * Check comments in the ticket: DISTRELEC-32221
     *
     * @param cmsSite
     * @return set of all visible categories for a site
     */
    Set<String> getAllVisibleCategoryCodes(CMSSiteModel cmsSite);
}

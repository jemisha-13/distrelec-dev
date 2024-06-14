/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.category;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.namics.distrelec.b2b.core.model.CategoryCountryModel;
import com.namics.distrelec.b2b.core.model.DistPimCategoryTypeModel;

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * This interface provides methods to handling the category stuff.
 * 
 * @author pnueesch, Namics AG
 * @since Namics Extensions 1.0
 */
public interface DistCategoryService extends CategoryService {

    /**
     * Gets the country specific category information. The current country is identified by the current CMSSite.
     * 
     * @param category
     * @return CategoryCountryModel
     */
    CategoryCountryModel getCountrySpecificCategoryInformation(final CategoryModel category);

    /**
     * Gets the country specific category information.
     * 
     * @param category
     * @param country
     * @return CategoryCountryModel
     */
    CategoryCountryModel getCountrySpecificCategoryInformation(final CategoryModel category, final CountryModel country);

    /**
     * Returns all super categories which are product categories (not classification classes).
     * 
     * @param category
     *            the base category
     * @return list of product categories
     */
    List<CategoryModel> getProductSuperCategories(final CategoryModel category);

    /**
     * Returns the category model.
     * 
     * @param name
     *            name of the category
     * @return category model
     */
    List<CategoryModel> getCategoriesForName(final String name);

    /**
     * Returns categories list using codes
     * 
     * @param list
     *            of category code
     * @return list of categories
     */
    List<CategoryModel> getCategoriesForCode(final List<String> codes);

    /**
     * Fetch categories that does not have any included product nor sub-categories
     * 
     * @return a list of {@code CategoryModel}
     */
    List<CategoryModel> getEmptyCategories();

    /**
     * Retrieve all categories for the category index. The categories are sorted by level and name.
     * 
     * @return a list of {@code CategoryModel}
     */
    List<CategoryModel> getCategoryByLevelRange(final int from, final int to);

    /**
     * Check whether the specified category is empty or not for the current CMSSite
     * 
     * @param category
     * @return {@code true} if the specified category is empty for the current CMSSite, {@code false} otherwise.
     * @see #isCategoryEmptyForCMSSite(CategoryModel, CMSSiteModel)
     */
    boolean isCategoryEmptyForCurrentSite(final CategoryModel category);

    /**
     * Check whether the category, given by its code, is empty or not for the current CMSSite
     * 
     * @param catCode
     * @return {@code true} if the category, given by its code, is empty for the current CMSSite, {@code false} otherwise.
     * @see #isCategoryEmptyForCurrentSite(CategoryModel)
     */
    boolean isCategoryEmptyForCurrentSite(final String catCode);

    /**
     * Check whether the specified category is empty or not for the specified CMSSite
     * 
     * @param category
     * @param cmsSite
     * @return {@code true} if the specified category is empty for the specified CMSSite, {@code false} otherwise.
     */
    boolean isCategoryEmptyForCMSSite(final CategoryModel category, final CMSSiteModel cmsSite);

    /**
     * Retrieve the list of {@code CMSSiteModel} in which the category is active.
     * 
     * @param category
     *            the target category
     * @return a list of {@code CMSSiteModel}
     */
    List<CMSSiteModel> getAvailableCMSSitesForCategory(final CategoryModel category);

    /**
     * Check whether there is any category having a predecessor the category given by its code. If multiple successors found, then the one
     * with the latest predecessor time-stamp will be returned.
     * 
     * @param code
     *            the predecessor category code
     * @return the successor, if any, of the category given by its code. {@code null} of the category has no successor.
     */
    CategoryModel findSuccessor(final String code);

    boolean hasSuccessor(final String code);

    /**
     * Check whether the category has at least one product or sub-category.
     * 
     * <b>Note</b> This method does not check the sales status nor punchouts.
     * 
     * @param category
     * @return {@code true} if the category has at least one product or sub-category, {@code false} otherwise.
     */
    boolean hasProductsOrSubCategories(final CategoryModel category);

    /**
     * Gets all non-empty Categories which are down in the category tree, {@code subTreeRoot} included and belong to the same catalogVersion
     * of {@code subTreeRoot}
     * 
     * @param subTreeRoot
     *            the categoryModel root of the subtree we want to get
     * @return all the categories in the subtree
     */
    Collection<CategoryModel> getCategoriesInTree(final CategoryModel subTreeRoot);

    /**
     * Gets all non-empty Categories which are down in the category tree, {@code subTreeRoot} included and belong to the same catalogVersion
     * of {@code subTreeRoot}
     * 
     * @param subTreeRootCode
     *            the code of the CategoryModel root of the subtree we want to get
     * @return all the categories in the subtree
     */
    Collection<CategoryModel> getCategoriesInTree(final String subTreeRootCode);

    boolean isProductLine(final CategoryModel category);

    /**
     * Search with pagination for categories by term
     *
     * @param term
     * @param pageSize
     * @return
     */
    SearchResult<CategoryModel> searchCategories(String term, int page, int pageSize);

    Optional<CategoryModel> findProductFamily(String code);

    boolean isProductFamily(CategoryModel category);

    boolean hasMultipleProductsInFamily(String code);

    Set<String> getAllVisibleCategoryCodes(CMSSiteModel cmsSite);

    List<CategoryModel> getBreadcrumbCategoriesInReverseOrderForCategory(final CategoryModel category);

    boolean categoryHasVisibleProduct(CategoryModel category, CMSSiteModel cmsSite);

}

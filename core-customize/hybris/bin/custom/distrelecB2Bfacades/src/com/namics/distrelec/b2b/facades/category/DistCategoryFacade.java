/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.category;

import java.util.Collection;
import java.util.List;

import com.namics.distrelec.b2b.facades.category.data.DistCategoryPageData;
import com.namics.distrelec.b2b.facades.message.queue.data.RelatedData;
import com.namics.distrelec.b2b.facades.product.data.DistCategoryIndexData;
import com.namics.hybris.ffsearch.data.facet.CategoryDisplayData;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetValueData;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Distrelec category facade.
 *
 * @author sivakumaran, Namics AG
 *
 */
public interface DistCategoryFacade {

    /**
     * Gets the sub categories from the given source category and returns it
     * truncated in a DTO.
     *
     * @param categoryModel
     * @return sub categories from the given source category, truncated in a
     *         DTO.
     */
    DistCategoryPageData getCategoryPageData(final CategoryModel categoryModel);

    /**
     * @return a list of {@code CategoryData}
     */
    List<DistCategoryIndexData> getCategoryIndex();

    /**
     * Checks whether a category is empty for the current CMSSite.
     *
     * @return {@code true} if the category is empty for the current CMSSite,
     *         {@code false} otherwise.
     */
    boolean isCategoryEmptyForCurrentSite(final CategoryModel category);

    /**
     * Checks whether the category, given by it's code, is empty for the current
     * CMSSite
     *
     * @param catCode
     * @return {@code true} if the category, given by its code, is empty for the
     *         current CMSSite, {@code false} otherwise.
     * @see #isCategoryEmptyForCurrentSite(CategoryModel)
     */
    boolean isCategoryEmptyForCurrentSite(final String catCode);

    /**
     * @param categoryValuesDate
     *            is the list of category which comes in the
     *            response of ff search result
     * @return rearrange the category data to group on there parent level
     */
    Collection<CategoryDisplayData<SearchStateData>> createCategoryDisplayData(
                                                                               final List<FactFinderFacetValueData<SearchStateData>> categoryValuesDate);

    /**
     * Retrieve the list of {@code CMSSite}s where the category is active
     *
     * @param category
     *            the target category
     * @return a list of {@code CMSSite}
     */
    List<CMSSiteModel> getAvailableCMSSitesForCategory(final CategoryModel category);

    void getSubCategoryPageData(CategoryModel category,
                                Collection<CategoryDisplayData<SearchStateData>> categoryDisplayDataList);

    /**
     * Search for categories
     *
     * @param term
     * @return
     */
    SearchResult<CategoryData> searchCategory(String term, int page, int pageSize);

    /**
     * Find Category by code
     *
     * @param code
     * @return
     */
    CategoryData findCategory(String code);

    /**
     * Returns the data page for category with parameter cateryCode
     *
     * @param categoryCode
     *            code/uid of category
     * @return a DistCategoryPageDate with source and subcategories
     */
    DistCategoryPageData getCategoryPageDataForOCC(final String categoryCode);

    RelatedData findCategoryRelatedData(final CategoryModel category);
}

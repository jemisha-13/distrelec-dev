/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.response.helper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Helper class with methods used to transform categories of {@link com.namics.hybris.ffsearch.data.search.SearchQueryData}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class CategoryNavigationHelper {

    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private FlexibleSearchService flexibleSearchService;

    public CategoryModel getCategoryForNameDownwards(final String baseCategoryCode, final String categoryName) {
        final CategoryModel baseCategory = categoryService.getCategoryForCode(baseCategoryCode);

        // Try finding category on lower levels
        return getSubCategoryForName(baseCategory, categoryName);
    }

    protected CategoryModel getSubCategoryForName(final CategoryModel baseCategory, final String categoryName) {
        // First try to find category in direct sub categories of the base category
        for (CategoryModel subCategory : baseCategory.getCategories()) {
            if (subCategory.getName() != null && subCategory.getName().equals(categoryName)) {
                return subCategory;
            }
        }
        
        // if not found, do try to find it via flex search
        final StringBuilder query = new StringBuilder();
        final Map<String, Object> params = new HashMap<String, Object>();

        query.append("SELECT {c.").append(CategoryModel.PK).append("} FROM {").append(CategoryModel._TYPECODE)
                .append(" as c} WHERE {c." + CategoryModel.NAME + "}=(?").append(CategoryModel.NAME).append(")");
        query.append(" AND {c." + CategoryModel.LEVEL + "} > (?").append(CategoryModel.LEVEL)
                .append(")");
        params.put(CategoryModel.NAME, categoryName);
        params.put(CategoryModel.LEVEL, baseCategory.getLevel());
        final SearchResult<CategoryModel> result = flexibleSearchService.search(query.toString(), params);
        if (result == null || CollectionUtils.isEmpty(result.getResult())) {
            return null;
        }
        else if (result.getResult().size() == 1) {
            return result.getResult().get(0);
        }
        else if (result.getResult().size() > 1) {
            // if not found in the direct sub categories, try to find in all sub categories
            Collection<CategoryModel> allSubCategories = baseCategory.getAllSubcategories();
            for (CategoryModel subCategory : result.getResult()) {
                if (allSubCategories.contains(subCategory)) {
                    return subCategory;
                }
            }
        }
        return null;
    }

}

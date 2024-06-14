/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.message.queue.strategies;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.category.DistCategoryService;

import de.hybris.platform.category.model.CategoryModel;

public class SubCategoriesCountProcessCategoryDirectlyOrRecursivelyStrategy implements ProcessCategoryDirectlyOrRecursivelyStrategy {

    private static final Logger LOG = LogManager.getLogger(SubCategoriesCountProcessCategoryDirectlyOrRecursivelyStrategy.class);

    public static final int CATEGORY_TREE_SIZE_TRESHOLD = 1000;

    @Autowired
    private DistCategoryService distCategoryService;

    @Override
    public boolean shouldProcessDirectly(final CategoryModel category) {
        final int categoryTreeSize = getDistCategoryService().getCategoriesInTree(category).size();
        final boolean result = categoryTreeSize <= CATEGORY_TREE_SIZE_TRESHOLD;
        LOG.debug("category: {} has categoryTreeSize: {}, shouldProcessDirectly: {}", category.getCode(), categoryTreeSize, result);
        return result;
    }

    @Override
    public Collection<CategoryModel> getCategoriesToSearchInto(final CategoryModel categoryModel) {
        return getDistCategoryService().getCategoriesInTree(categoryModel);
    }

    public DistCategoryService getDistCategoryService() {
        return distCategoryService;
    }

    public void setDistCategoryService(final DistCategoryService distCategoryService) {
        this.distCategoryService = distCategoryService;
    }

}

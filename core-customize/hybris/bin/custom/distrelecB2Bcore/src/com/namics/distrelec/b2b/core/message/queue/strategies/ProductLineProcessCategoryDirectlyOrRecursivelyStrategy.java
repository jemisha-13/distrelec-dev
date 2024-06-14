/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.message.queue.strategies;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.category.DistCategoryService;

import de.hybris.platform.category.model.CategoryModel;

public class ProductLineProcessCategoryDirectlyOrRecursivelyStrategy implements ProcessCategoryDirectlyOrRecursivelyStrategy {

    @Autowired
    private DistCategoryService distCategoryService;

    @Override
    public boolean shouldProcessDirectly(final CategoryModel category) {
        return getDistCategoryService().isProductLine(category);
    }

    @Override
    public Collection<CategoryModel> getCategoriesToSearchInto(final CategoryModel categoryModel) {
        return Collections.singleton(categoryModel);
    }

    public DistCategoryService getDistCategoryService() {
        return distCategoryService;
    }

    public void setDistCategoryService(final DistCategoryService distCategoryService) {
        this.distCategoryService = distCategoryService;
    }

}

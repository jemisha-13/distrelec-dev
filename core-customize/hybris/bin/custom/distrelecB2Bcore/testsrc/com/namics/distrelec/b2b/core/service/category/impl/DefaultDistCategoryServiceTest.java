/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.category.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.category.model.CategoryModel;

/**
 * Tests the {@link DefaultDistCategoryService} class.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
@UnitTest
public class DefaultDistCategoryServiceTest {

    private final DefaultDistCategoryService defaultDistCategoryService = new DefaultDistCategoryService();

    @Test
    public void testGetProductSuperCategories() {
        // Init
        final CatalogVersionModel catalogVersion = new CatalogVersionModel();
        final CategoryModel category = new CategoryModel();
        category.setCatalogVersion(catalogVersion);
        final CategoryModel superCategory = new CategoryModel();
        superCategory.setCatalogVersion(catalogVersion);
        category.setSupercategories(Collections.singletonList(superCategory));

        // Action
        final List<CategoryModel> productSuperCategories = defaultDistCategoryService.getProductSuperCategories(category);

        // Evaluation
        Assert.assertEquals(1, productSuperCategories.size());
        Assert.assertEquals(superCategory, productSuperCategories.get(0));
    }

    @Test
    public void testGetProductSuperCategoriesWithCla() {
        // Init
        final CatalogVersionModel catalogVersion = new CatalogVersionModel();

        final CategoryModel category = new CategoryModel();
        category.setCatalogVersion(catalogVersion);

        final CategoryModel superCategory = new CategoryModel();
        superCategory.setCatalogVersion(catalogVersion);

        final ClassificationSystemVersionModel classificationSystemVersion = new ClassificationSystemVersionModel();

        final ClassificationClassModel superClassificationClass = new ClassificationClassModel();
        superClassificationClass.setCatalogVersion(classificationSystemVersion);

        category.setSupercategories(Arrays.asList(superCategory, superClassificationClass));

        // Action
        final List<CategoryModel> productSuperCategories = defaultDistCategoryService.getProductSuperCategories(category);

        // Evaluation
        Assert.assertEquals(1, productSuperCategories.size());
        Assert.assertEquals(superCategory, productSuperCategories.get(0));
    }

}

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
package com.namics.distrelec.b2b.facades.suggestion.impl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.suggestion.SimpleSuggestionService;
import com.namics.distrelec.b2b.facades.suggestion.SimpleSuggestionFacade;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Default implementation of {@link SimpleSuggestionFacade}.
 */
public class DefaultSimpleSuggestionFacade implements SimpleSuggestionFacade {
    private UserService userService;

    private CategoryService categoryService;

    private Populator<ProductModel, ProductData> productPrimaryImagePopulator;

    private Populator<ProductModel, ProductData> productPricePopulator;

    private Populator<ProductModel, ProductData> productPopulator;

    private SimpleSuggestionService simpleSuggestionService;

    @Override
    public List<ProductData> getReferencesForPurchasedInCategory(final String categoryCode, final ProductReferenceTypeEnum referenceType,
                                                                 final boolean excludePurchased, final Integer limit) {
        final UserModel user = getUserService().getCurrentUser();
        final CategoryModel category = getCategoryService().getCategoryForCode(categoryCode);

        final List<ProductModel> products = getSimpleSuggestionService().getReferencesForPurchasedInCategory(category, user, referenceType, excludePurchased,
                                                                                                             limit);

        final List<ProductData> result = new LinkedList<>();
        for (final ProductModel productModel : products) {
            final ProductData productData = new ProductData();
            getProductPopulator().populate(productModel, productData);
            getProductPricePopulator().populate(productModel, productData);
            getProductPrimaryImagePopulator().populate(productModel, productData);

            result.add(productData);
        }
        return result;
    }

    protected UserService getUserService() {
        return userService;
    }

    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    protected CategoryService getCategoryService() {
        return categoryService;
    }

    @Required
    public void setCategoryService(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    protected Populator<ProductModel, ProductData> getProductPrimaryImagePopulator() {
        return productPrimaryImagePopulator;
    }

    @Required
    public void setProductPrimaryImagePopulator(final Populator<ProductModel, ProductData> productPrimaryImagePopulator) {
        this.productPrimaryImagePopulator = productPrimaryImagePopulator;
    }

    protected Populator<ProductModel, ProductData> getProductPricePopulator() {
        return productPricePopulator;
    }

    @Required
    public void setProductPricePopulator(final Populator<ProductModel, ProductData> productPricePopulator) {
        this.productPricePopulator = productPricePopulator;
    }

    protected Populator<ProductModel, ProductData> getProductPopulator() {
        return productPopulator;
    }

    @Required
    public void setProductPopulator(final Populator<ProductModel, ProductData> productPopulator) {
        this.productPopulator = productPopulator;
    }

    protected SimpleSuggestionService getSimpleSuggestionService() {
        return simpleSuggestionService;
    }

    @Required
    public void setSimpleSuggestionService(final SimpleSuggestionService simpleSuggestionService) {
        this.simpleSuggestionService = simpleSuggestionService;
    }
}

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

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.core.suggestion.SimpleSuggestionService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPricePopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPrimaryImagePopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Unit test for {@link DefaultSimpleSuggestionFacade}
 */
@UnitTest
public class DefaultSimpleSuggestionFacadeTest {
    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private SimpleSuggestionService simpleSuggestionService;

    private ProductModel productModel = new ProductModel();

    @Mock
    private ProductPopulator productPopulator;

    @Mock
    private ProductPricePopulator productPricePopulator;

    @Mock
    private ProductPrimaryImagePopulator productPrimaryImagePopulator;

    private DefaultSimpleSuggestionFacade defaultSimpleSuggestionFacade;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultSimpleSuggestionFacade = new DefaultSimpleSuggestionFacade();
        defaultSimpleSuggestionFacade.setUserService(userService);
        defaultSimpleSuggestionFacade.setCategoryService(categoryService);
        defaultSimpleSuggestionFacade.setSimpleSuggestionService(simpleSuggestionService);
        defaultSimpleSuggestionFacade.setProductPopulator(productPopulator);
        defaultSimpleSuggestionFacade.setProductPricePopulator(productPricePopulator);
        defaultSimpleSuggestionFacade.setProductPrimaryImagePopulator(productPrimaryImagePopulator);
    }

    @Test
    public void testGetReferencedProductsForBoughtCategory() {

        productModel.setCode("100100100");
        productModel.setEan("Test");
        final UserModel user = mock(UserModel.class);
        final CategoryModel category = mock(CategoryModel.class);

        final String categoryCode = "code";
        given(categoryService.getCategoryForCode(categoryCode)).willReturn(category);
        final Integer limit = NumberUtils.INTEGER_ONE;
        final boolean excludeBoughtProducts = true;
        final ProductReferenceTypeEnum type = ProductReferenceTypeEnum.FOLLOWUP;
        given(userService.getCurrentUser()).willReturn(user);
        given(simpleSuggestionService.getReferencesForPurchasedInCategory(category, user, type, excludeBoughtProducts, limit))
                                                                                                                              .willReturn(Collections.singletonList(productModel));

        final List<ProductData> result = defaultSimpleSuggestionFacade.getReferencesForPurchasedInCategory(categoryCode, type, excludeBoughtProducts, limit);

        verify(productPricePopulator, BDDMockito.times(1)).populate(eq(productModel), any(ProductData.class));
        verify(productPrimaryImagePopulator, BDDMockito.times(1)).populate(eq(productModel), any(ProductData.class));
    }
}

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
package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.facades.suggestion.SimpleSuggestionFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorcms.model.components.PurchasedProductReferencesComponentModel;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit test for {@link PurchasedProductReferenceComponentController}
 */
@UnitTest
public class PurchasedProductReferenceComponentControllerTest {
    private static final String COMPONENT_UID = "componentUid";
    private static final String TEST_COMPONENT_UID = "componentUID";
    private static final String TEST_TYPE_CODE = PurchasedProductReferencesComponentModel._TYPECODE;
    private static final String TEST_TYPE_VIEW = ControllerConstants.Views.Cms.ComponentPrefix + StringUtils.lowerCase(TEST_TYPE_CODE);
    private static final String TITLE = "title";
    private static final String TITLE_VALUE = "Accessories";
    private static final String PRODUCT_REFERENCES = "productReferences";
    private static final String COMPONENT = "component";
    private static final String CATEGORY_CODE = "CategoryCode";

    private PurchasedProductReferenceComponentController purchasedProductReferenceComponentController;

    @Mock
    private PurchasedProductReferencesComponentModel purchasedProductReferencesComponentModel;
    @Mock
    private Model model;
    @Mock
    private DefaultCMSComponentService cmsComponentService;
    @Mock
    private SimpleSuggestionFacade simpleSuggestionFacade;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ProductData productData;
    @Mock
    private CategoryModel categoryModel;

    private final List<ProductData> productDataList = Collections.singletonList(productData);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        purchasedProductReferenceComponentController = new PurchasedProductReferenceComponentController();
        purchasedProductReferenceComponentController.setCmsComponentService(cmsComponentService);
        ReflectionTestUtils.setField(purchasedProductReferenceComponentController, "b2bSimpleSuggestionFacade", simpleSuggestionFacade);
    }

    @Test
    public void testRenderComponent() throws Exception {
        given(purchasedProductReferencesComponentModel.getMaximumNumberProducts()).willReturn(Integer.valueOf(1));
        given(purchasedProductReferencesComponentModel.getTitle()).willReturn(TITLE_VALUE);
        given(purchasedProductReferencesComponentModel.getProductReferenceType()).willReturn(ProductReferenceTypeEnum.ACCESSORIES);
        given(purchasedProductReferencesComponentModel.getCategory()).willReturn(categoryModel);
        given(categoryModel.getCode()).willReturn(CATEGORY_CODE);
        given(Boolean.valueOf(purchasedProductReferencesComponentModel.isFilterPurchased())).willReturn(Boolean.TRUE);

        given(simpleSuggestionFacade.getReferencesForPurchasedInCategory(Mockito.anyString(), Mockito.any(ProductReferenceTypeEnum.class), Mockito.anyBoolean(),
                Mockito.<Integer>any())).willReturn(productDataList);

        final String viewName = purchasedProductReferenceComponentController.handleComponent(request, response, model,
                purchasedProductReferencesComponentModel);
        verify(model, Mockito.times(1)).addAttribute(TITLE, TITLE_VALUE);
        verify(model, Mockito.times(1)).addAttribute(PRODUCT_REFERENCES, productDataList);
        Assert.assertEquals(TEST_TYPE_VIEW, viewName);
    }

    @Test
    public void testRenderComponentUid() throws Exception {
        given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
        given(cmsComponentService.getAbstractCMSComponent(TEST_COMPONENT_UID)).willReturn(purchasedProductReferencesComponentModel);
        given(purchasedProductReferencesComponentModel.getMaximumNumberProducts()).willReturn(Integer.valueOf(1));
        given(purchasedProductReferencesComponentModel.getTitle()).willReturn(TITLE_VALUE);
        given(purchasedProductReferencesComponentModel.getProductReferenceType()).willReturn(ProductReferenceTypeEnum.ACCESSORIES);
        given(purchasedProductReferencesComponentModel.getCategory()).willReturn(categoryModel);
        given(categoryModel.getCode()).willReturn(CATEGORY_CODE);
        given(Boolean.valueOf(purchasedProductReferencesComponentModel.isFilterPurchased())).willReturn(Boolean.TRUE);

        given(simpleSuggestionFacade.getReferencesForPurchasedInCategory(Mockito.anyString(), Mockito.any(ProductReferenceTypeEnum.class), Mockito.anyBoolean(),
                Mockito.<Integer>any())).willReturn(productDataList);

        final String viewName = purchasedProductReferenceComponentController.handleGet(request, response, model);
        verify(model, Mockito.times(1)).addAttribute(COMPONENT, purchasedProductReferencesComponentModel);
        verify(model, Mockito.times(1)).addAttribute(TITLE, TITLE_VALUE);
        verify(model, Mockito.times(1)).addAttribute(PRODUCT_REFERENCES, productDataList);
        Assert.assertEquals(TEST_TYPE_VIEW, viewName);
    }

    @Test(expected = AbstractController.HttpNotFoundException.class)
    public void testRenderComponentNotFound() throws Exception {
        given(request.getAttribute(COMPONENT_UID)).willReturn(null);
        given(request.getParameter(COMPONENT_UID)).willReturn(null);
        purchasedProductReferenceComponentController.handleGet(request, response, model);
    }

    @Test(expected = AbstractController.HttpNotFoundException.class)
    public void testRenderComponentNotFound2() throws Exception {
        given(request.getAttribute(COMPONENT_UID)).willReturn(null);
        given(request.getParameter(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
        given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(null);
        purchasedProductReferenceComponentController.handleGet(request, response, model);
    }

    @Test(expected = AbstractController.HttpNotFoundException.class)
    public void testRenderComponentNotFound3() throws Exception {
        given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
        given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(null);
        given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willThrow(new CMSItemNotFoundException(""));
        purchasedProductReferenceComponentController.handleGet(request, response, model);
    }

}

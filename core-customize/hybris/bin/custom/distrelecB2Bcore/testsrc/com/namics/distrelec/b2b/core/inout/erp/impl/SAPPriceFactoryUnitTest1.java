/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;

@UnitTest
public class SAPPriceFactoryUnitTest1 {

    @InjectMocks
    private final SAPPriceFactory sapPriceFactory = new SAPPriceFactory();

    @Mock
    private DistSalesOrgProductService distSalesOrgProductService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getMinimumOrderQuantityForProduct_OK() {

        // given
        final ProductModel mockedProduct = Mockito.mock(ProductModel.class);
        final DistSalesOrgProductModel distSalesOrgProductModel = Mockito.mock(DistSalesOrgProductModel.class);
        BDDMockito.given(distSalesOrgProductService.getCurrentSalesOrgProduct(mockedProduct)).willReturn(distSalesOrgProductModel);
        final long positiveMOQ = 5L;
        BDDMockito.given(distSalesOrgProductModel.getOrderQuantityMinimum()).willReturn(positiveMOQ);

        // when
        final Long outcome = sapPriceFactory.getMinimumOrderQuantityForProduct(mockedProduct);

        // then
        assertEquals(positiveMOQ, outcome.longValue());
    }

    @Test
    public void getMinimumOrderQuantityForProduct_NullDSOP() {

        // given
        final ProductModel mockedProduct = Mockito.mock(ProductModel.class);
        BDDMockito.given(distSalesOrgProductService.getCurrentSalesOrgProduct(mockedProduct)).willReturn(null);

        // when
        final Long outcome = sapPriceFactory.getMinimumOrderQuantityForProduct(mockedProduct);

        // then
        assertEquals(SAPPriceFactory.DEFAULT_ORDER_QUANTITY_MINIMUM, outcome.longValue());
    }

    @Test
    public void getMinimumOrderQuantityForProduct_NullMOQ() {

        // given
        final ProductModel mockedProduct = Mockito.mock(ProductModel.class);
        final DistSalesOrgProductModel distSalesOrgProductModel = Mockito.mock(DistSalesOrgProductModel.class);
        BDDMockito.given(distSalesOrgProductService.getCurrentSalesOrgProduct(mockedProduct)).willReturn(distSalesOrgProductModel);
        BDDMockito.given(distSalesOrgProductModel.getOrderQuantityMinimum()).willReturn(null);

        // when
        final Long outcome = sapPriceFactory.getMinimumOrderQuantityForProduct(mockedProduct);

        // then
        assertEquals(SAPPriceFactory.DEFAULT_ORDER_QUANTITY_MINIMUM, outcome.longValue());
    }

    @Test
    public void getMinimumOrderQuantityForProduct_NegativeMOQ() {

        // given
        final ProductModel mockedProduct = Mockito.mock(ProductModel.class);
        final DistSalesOrgProductModel distSalesOrgProductModel = Mockito.mock(DistSalesOrgProductModel.class);
        BDDMockito.given(distSalesOrgProductService.getCurrentSalesOrgProduct(mockedProduct)).willReturn(distSalesOrgProductModel);
        BDDMockito.given(distSalesOrgProductModel.getOrderQuantityMinimum()).willReturn(-5L);

        // when
        final Long outcome = sapPriceFactory.getMinimumOrderQuantityForProduct(mockedProduct);

        // then
        assertEquals(SAPPriceFactory.DEFAULT_ORDER_QUANTITY_MINIMUM, outcome.longValue());
    }

}

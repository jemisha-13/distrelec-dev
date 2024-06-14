/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.core.inout.erp.CustomerPriceService;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.europe1.jalo.Europe1PriceFactory;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.user.User;

import static org.mockito.Mockito.when;

@UnitTest
public class SAPPriceFactoryUnitTest2 {

    @InjectMocks
    private final SAPPriceFactory mockedSapPriceFactory = Mockito.spy(new SAPPriceFactory());

    @Mock
    private CustomerPriceService customerPriceService;

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @Mock
    private ModelService modelService;

    @Mock
    private BaseStoreService baseStoreService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Currency currency = new Currency();
        CurrencyModel currencyModel = new CurrencyModel();
        BaseStoreModel baseStoreModel = new BaseStoreModel();
        baseStoreModel.setDefaultCurrency(currencyModel);
        when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
        when(modelService.getSource(currencyModel)).thenReturn(currency);
    }

    @Test
    public void getProductListPriceInformations_anonymous() throws JaloPriceFactoryException {
        
        // given
        final SessionContext ctx = Mockito.mock(SessionContext.class);
        final Europe1PriceFactory mockedEurope1PriceFactory = Mockito.mock(Europe1PriceFactory.class);
        final Product mockedProduct = Mockito.mock(Product.class);
        final User mockedcustomer = Mockito.mock(User.class);
        BDDMockito.given(mockedSapPriceFactory.getDefaultPriceFactory()).willReturn(mockedEurope1PriceFactory);
        BDDMockito.given(ctx.getUser()).willReturn(mockedcustomer);
        Mockito.doReturn(false).when(mockedSapPriceFactory).userRequiresCalculation(mockedcustomer);

        // when
        mockedSapPriceFactory.getProductListPriceInformations(ctx, mockedProduct, new Date(), true, true);
        
        //then
        BDDMockito.verify(mockedSapPriceFactory, Mockito.times(0)).filterPricesBelowMoq(Mockito.anyList(), Mockito.anyLong());
        BDDMockito.verify(customerPriceService, Mockito.times(0)).getOnlinePriceList(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.anyString(),
                Mockito.anyLong());
        BDDMockito.verify(customerPriceService, Mockito.times(0)).getPricesForPricesList(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                Mockito.anyString(), Mockito.anyList());
    }

    @Test
    public void getProductListPriceInformations_WithPrices() throws JaloPriceFactoryException {

        // given
        final SessionContext ctx = Mockito.mock(SessionContext.class);
        final Europe1PriceFactory mockedEurope1PriceFactory = Mockito.mock(Europe1PriceFactory.class);
        final Product mockedProduct = Mockito.mock(Product.class);
        final User mockedcustomer = Mockito.mock(User.class);

        BDDMockito.given(mockedSapPriceFactory.getDefaultPriceFactory()).willReturn(mockedEurope1PriceFactory);
        BDDMockito.given(ctx.getUser()).willReturn(mockedcustomer);
        final PriceInformation hybrisPrice = Mockito.mock(PriceInformation.class);
        final List<PriceInformation> hybrisPrices = Arrays.asList(hybrisPrice);
        Mockito.doReturn(hybrisPrices) //
                .when(mockedSapPriceFactory) //
                .getPriceInformations(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyBoolean(),
                        Mockito.any(), Mockito.anyList());
        Mockito.doReturn(true).when(mockedSapPriceFactory).userRequiresCalculation(mockedcustomer);
        Mockito.doReturn("dummyErpCustomerId").when(mockedSapPriceFactory).getErpCustomerID(mockedcustomer);
        BDDMockito.given(distSalesOrgService.getCurrentSalesOrg()).willReturn(Mockito.mock(DistSalesOrgModel.class));
        BDDMockito.given(ctx.getCurrency()).willReturn(Mockito.mock(Currency.class));
        Mockito.doReturn(5L).when(mockedSapPriceFactory).getMinimumOrderQuantityForProduct(mockedProduct);
        Mockito.doReturn(hybrisPrices).when(mockedSapPriceFactory).filterPricesBelowMoq(Mockito.eq(hybrisPrices),
                Mockito.anyLong());

        // when
        mockedSapPriceFactory.getProductListPriceInformations(ctx, mockedProduct, new Date(), true, true);

        // then
        BDDMockito.verify(mockedSapPriceFactory, Mockito.times(1)).filterPricesBelowMoq(Mockito.eq(hybrisPrices), Mockito.anyLong());
        BDDMockito.verify(customerPriceService, Mockito.times(0)).getOnlinePriceList(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.anyString(),
                Mockito.anyLong());
        BDDMockito.verify(customerPriceService, Mockito.times(1)).getPricesForPricesList(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                Mockito.anyString(), Mockito.anyList());
    }

    @Test
    public void getProductListPriceInformations_WithPriceBelowMoq() throws JaloPriceFactoryException {

        // given
        final SessionContext ctx = Mockito.mock(SessionContext.class);
        final Europe1PriceFactory mockedEurope1PriceFactory = Mockito.mock(Europe1PriceFactory.class);
        final Product mockedProduct = Mockito.mock(Product.class);
        final User mockedcustomer = Mockito.mock(User.class);
        BDDMockito.given(mockedSapPriceFactory.getDefaultPriceFactory()).willReturn(mockedEurope1PriceFactory);
        BDDMockito.given(ctx.getUser()).willReturn(mockedcustomer);
        final PriceInformation hybrisPrice = Mockito.mock(PriceInformation.class);
        final List<PriceInformation> hybrisPrices = Arrays.asList(hybrisPrice);
        Mockito.doReturn(hybrisPrices) //
                .when(mockedSapPriceFactory) //
                .getPriceInformations(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyBoolean(),
                        Mockito.any(), Mockito.anyList());
        Mockito.doReturn(true).when(mockedSapPriceFactory).userRequiresCalculation(mockedcustomer);
        Mockito.doReturn("dummyErpCustomerId").when(mockedSapPriceFactory).getErpCustomerID(mockedcustomer);
        BDDMockito.given(distSalesOrgService.getCurrentSalesOrg()).willReturn(Mockito.mock(DistSalesOrgModel.class));
        BDDMockito.given(ctx.getCurrency()).willReturn(Mockito.mock(Currency.class));
        Mockito.doReturn(5L).when(mockedSapPriceFactory).getMinimumOrderQuantityForProduct(mockedProduct);
        Mockito.doReturn(Collections.emptyList()).when(mockedSapPriceFactory).filterPricesBelowMoq(Mockito.anyList(), Mockito.anyLong());

        // when
        mockedSapPriceFactory.getProductListPriceInformations(ctx, mockedProduct, new Date(), true, true);

        // then
        BDDMockito.verify(mockedSapPriceFactory, Mockito.times(1)).filterPricesBelowMoq(Mockito.eq(hybrisPrices), Mockito.anyLong());
        BDDMockito.verify(customerPriceService, Mockito.times(1)).getOnlinePriceList(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.anyString(),
                Mockito.anyLong());
        BDDMockito.verify(customerPriceService, Mockito.times(0)).getPricesForPricesList(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                Mockito.anyString(), Mockito.anyList());
    }


}

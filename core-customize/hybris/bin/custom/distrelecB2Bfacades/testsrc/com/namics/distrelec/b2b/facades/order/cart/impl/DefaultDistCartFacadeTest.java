package com.namics.distrelec.b2b.facades.order.cart.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@IntegrationTest
public class DefaultDistCartFacadeTest extends ServicelayerTransactionalTest {

    @Resource
    private DefaultDistCartFacade defaultDistCartFacade;

    @Before
    public void setUp() throws Exception {
        importCsv("/distrelecB2Bfacades/test/testDistCartFacade.impex", "utf-8");
    }

    @Test
    public void getFreeShippingValueReturnValueAndCurrency() {
       PriceData freeShippingValue = defaultDistCartFacade.getFreeShippingValue(BigDecimal.valueOf(30.0), "CHF");

       assertEquals(BigDecimal.valueOf(30.0), freeShippingValue.getValue());
       assertEquals("CHF", freeShippingValue.getCurrencyIso());
    }

    @Test
    public void getFreeShippingValueFormattedValueIsNull() {
        PriceData freeShippingValue = defaultDistCartFacade.getFreeShippingValue(BigDecimal.valueOf(30.0), "CHF");

       assertNull(freeShippingValue.getFormattedValue());
    }
}

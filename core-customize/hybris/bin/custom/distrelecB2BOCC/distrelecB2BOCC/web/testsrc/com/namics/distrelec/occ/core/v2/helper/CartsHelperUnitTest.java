package com.namics.distrelec.occ.core.v2.helper;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CartsHelperUnitTest {

    private CartsHelper cartsHelper = new CartsHelper();

    @Test
    public void testGetJoinedBlockedProducts() {
        // given
        List<CartModificationData> data = prepareCartModificationData();

        // when
        String result = cartsHelper.getJoinedBlockedProducts(data);

        // then
        assertThat(result, equalTo("300400500,300400600"));
    }

    @Test
    public void testGetJoinedBlockedProductsWithNoBlockedProducts() {
        // given
        List<CartModificationData> blockedProducts = Collections.emptyList();

        // when
        String result = cartsHelper.getJoinedBlockedProducts(blockedProducts);

        // then
        assertThat(result, equalTo(StringUtils.EMPTY));
    }

    private List<CartModificationData> prepareCartModificationData() {
        CartModificationData cartModificationData1 = mock(CartModificationData.class);
        OrderEntryData orderEntryData1 = mock(OrderEntryData.class);
        ProductData productData1 = mock(ProductData.class);

        when(cartModificationData1.getEntry()).thenReturn(orderEntryData1);
        when(orderEntryData1.getProduct()).thenReturn(productData1);
        when(productData1.getCode()).thenReturn("300400500");

        CartModificationData cartModificationData2 = mock(CartModificationData.class);
        OrderEntryData orderEntryData2 = mock(OrderEntryData.class);
        ProductData productData2 = mock(ProductData.class);

        when(cartModificationData2.getEntry()).thenReturn(orderEntryData2);
        when(orderEntryData2.getProduct()).thenReturn(productData2);
        when(productData2.getCode()).thenReturn("300400600");

        return List.of(cartModificationData1, cartModificationData2);
    }
}

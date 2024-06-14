package com.namics.distrelec.b2b.facades.order.converters;

import com.distrelec.webservice.if15.v1.CurrencyCode;
import com.distrelec.webservice.if15.v1.OrdersSearchLine;
import com.namics.distrelec.b2b.core.model.DistOrderStatusModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.core.enums.OrderStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapSearchOrderHistoryConverterUnitTest {

    @Mock
    private DistrelecCodelistService codelistService;

    @Mock
    private PriceDataFactory priceDataFactory;

    @InjectMocks
    SapSearchOrderHistoryConverter sapSearchOrderHistoryConverter;

    @Test
    public void testPopulate() {
        // given
        OrdersSearchLine source = mock(OrdersSearchLine.class);
        OrderHistoryData target = new OrderHistoryData();
        DistOrderStatusModel distOrderStatusModel = mock(DistOrderStatusModel.class);

        // when
        when(source.getOrderId()).thenReturn("ORDER_ID");
        when(source.getOrderStatus()).thenReturn("ORDER_STATUS");
        when(codelistService.getDistOrderStatus("ORDER_STATUS")).thenReturn(distOrderStatusModel);
        when(distOrderStatusModel.getHybrisOrderStatus()).thenReturn(OrderStatus.APPROVED);
        when(source.getCurrencyCode()).thenReturn(CurrencyCode.CHF);
        when(source.getOrderTotal()).thenReturn(100.0);
        when(source.getContactName()).thenReturn("Distrelec Customer");
        when(source.getInvoiceIds()).thenReturn(Arrays.asList("123","456","789"));

        sapSearchOrderHistoryConverter.populate(source, target);

        // then
        assertThat(target.getCode(), equalTo("ORDER_ID"));
        assertThat(target.getStatus(), equalTo(OrderStatus.APPROVED));
        assertThat(target.getCurrency(), equalTo("CHF"));
        assertThat(target.getInvoiceIds(), equalTo("123, 456, 789"));
        assertThat(target.getUserFullName(), equalTo("Distrelec Customer"));
    }
}

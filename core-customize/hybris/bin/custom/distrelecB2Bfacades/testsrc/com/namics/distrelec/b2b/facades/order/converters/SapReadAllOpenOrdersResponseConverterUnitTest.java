package com.namics.distrelec.b2b.facades.order.converters;

import com.distrelec.webservice.if15.v1.CurrencyCode;
import com.distrelec.webservice.if15.v1.OpenOrders;
import com.namics.distrelec.b2b.core.model.DistOrderStatusModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.core.enums.OrderStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapReadAllOpenOrdersResponseConverterUnitTest {

    @Mock
    private DistrelecCodelistService codeListService;

    @Mock
    private PriceDataFactory priceDataFactory;

    @InjectMocks
    private SapReadAllOpenOrdersResponseConverter converter;

    @Test
    public void testPopulate() {
        OpenOrders source = createFullOpenOrders("Confirmed");
        OrderHistoryData target = new OrderHistoryData();

        converter.populate(source, target);

        assertThat(target.getCode(), equalTo(source.getOrderId()));
        assertThat(target.getCurrency(), equalTo(source.getCurrencyCode().name()));
        assertThat(target.getOrderReference(), equalTo(source.getCustomerReferenceHeaderLevel()));
        assertThat(target.getStatus(), equalTo(OrderStatus.APPROVED));

    }

    @Test
    public void testPopulateWithErpStatusUnknown() {
        OpenOrders source = createFullOpenOrders("");
        OrderHistoryData target = new OrderHistoryData();

        converter.populate(source, target);

        assertThat(target.getCode(), equalTo(source.getOrderId()));
        assertThat(target.getCurrency(), equalTo(source.getCurrencyCode().name()));
        assertThat(target.getOrderReference(), equalTo(source.getCustomerReferenceHeaderLevel()));
        assertThat(target.getStatus(), equalTo(OrderStatus.ERP_STATUS_UNKNOWN));

    }

    private OpenOrders createFullOpenOrders(String orderStatus) {
        OpenOrders openOrders = mock(OpenOrders.class);
        DistOrderStatusModel distOrderStatusModel = mock(DistOrderStatusModel.class);

        when(openOrders.getOrderId()).thenReturn("12345");
        when(openOrders.getCurrencyCode()).thenReturn(CurrencyCode.CHF);
        when(openOrders.getOrderStatus()).thenReturn(orderStatus);
        when(openOrders.getCustomerReferenceHeaderLevel()).thenReturn("Ref123");
        when(codeListService.getDistOrderStatus("Confirmed")).thenReturn(distOrderStatusModel);
        when(distOrderStatusModel.getHybrisOrderStatus()).thenReturn(OrderStatus.APPROVED);

        return openOrders;
    }
}

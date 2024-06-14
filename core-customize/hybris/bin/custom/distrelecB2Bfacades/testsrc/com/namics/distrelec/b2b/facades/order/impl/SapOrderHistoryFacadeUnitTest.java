package com.namics.distrelec.b2b.facades.order.impl;

import com.distrelec.webservice.if15.v1.OpenOrders;
import com.distrelec.webservice.if15.v1.OrderSearchRequestV2;
import com.distrelec.webservice.if15.v1.OrderSearchResponseV2;
import com.distrelec.webservice.if15.v1.OrdersSearchLine;
import com.distrelec.webservice.if15.v1.ReadAllOpenOrdersRequestV2;
import com.distrelec.webservice.if15.v1.ReadAllOpenOrdersResponseV2;
import com.distrelec.webservice.if15.v1.ReadOrderRequestV2;
import com.distrelec.webservice.if15.v1.ReadOrderResponseV2;
import com.distrelec.webservice.if19.v1.RMAGetListForOrderRequest;
import com.distrelec.webservice.if19.v1.RMAGetListForOrderResponse;
import com.distrelec.webservice.if19.v1.RMAGetOrderItemsRequest;
import com.distrelec.webservice.if19.v1.RMAGetOrderItemsResponse;
import com.namics.distrelec.b2b.core.inout.erp.OrderService;
import com.namics.distrelec.b2b.core.inout.erp.ReturnOnlineService;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.order.DistOrderService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.facades.search.converter.ReadOrderResponseWraper;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapOrderHistoryFacadeUnitTest {

    @Mock
    private OrderService sapOrderService;

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @Mock
    private UserService userService;

    @Mock
    private Converter<OpenOrders, OrderHistoryData> sapReadAllOpenOrdersResponseConverter;

    @Mock
    private Converter<OpenOrders, OrderData> sapReadAllOpenOrdersDetailsResponse;

    @Mock
    private ReturnOnlineService returnOnlineService;

    @Mock
    private Converter<ReadOrderResponseWraper, OrderData> sapReadOrderResponseOrderHistoryConverter;

    @Mock
    private DistOrderService distOrderService;

    @Mock
    private Converter<OrdersSearchLine, OrderHistoryData> sapSearchOrderHistoryConverter;

    @Mock
    private Converter<DistOrderHistoryPageableData, OrderSearchRequestV2> sapSearchOrderRequestConverter;

    @Mock
    private DistrelecCMSSiteService distrelecCMSSiteService;

    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private SapOrderHistoryFacade sapOrderHistoryFacade;

    @Test
    public void testGetOpenOrders() {
        // given
        DistOrderHistoryPageableData pageableData = new DistOrderHistoryPageableData();
        ReadAllOpenOrdersResponseV2 response = mock(ReadAllOpenOrdersResponseV2.class);
        OpenOrders openOrders = mock(OpenOrders.class);
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        B2BUnitModel b2bUnitModel = mock(B2BUnitModel.class);
        OrderHistoryData orderHistoryData = mock(OrderHistoryData.class);

        // when
        when(response.getOpenOrders()).thenReturn(List.of(openOrders));
        when(sapOrderService.readAllOpenOrders(any(ReadAllOpenOrdersRequestV2.class))).thenReturn(response);
        when(sapReadAllOpenOrdersResponseConverter.convert(openOrders)).thenReturn(orderHistoryData);
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7371");
        when(userService.getCurrentUser()).thenReturn(customer);
        when(customer.getDefaultB2BUnit()).thenReturn(b2bUnitModel);
        when(b2bUnitModel.getErpCustomerID()).thenReturn("3189283");

        SearchPageData<OrderHistoryData> result = sapOrderHistoryFacade.getOpenOrders(pageableData);

        // then
        verify(sapOrderService).readAllOpenOrders(any(ReadAllOpenOrdersRequestV2.class));
        verify(sapReadAllOpenOrdersResponseConverter).convert(openOrders);
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void testGetOpenOrderDetailsForCode() {
        // given
        String testCode = "12345";
        ReadAllOpenOrdersResponseV2 response = mock(ReadAllOpenOrdersResponseV2.class);
        OpenOrders matchingOpenOrder = mock(OpenOrders.class);
        OpenOrders nonMatchingOpenOrder = mock(OpenOrders.class);
        OrderData orderData = mock(OrderData.class);
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        B2BUnitModel b2bUnitModel = mock(B2BUnitModel.class);

        // when
        when(response.getOpenOrders()).thenReturn(Arrays.asList(matchingOpenOrder, nonMatchingOpenOrder));
        when(matchingOpenOrder.getOrderId()).thenReturn(testCode);
        when(sapOrderService.readAllOpenOrders(any(ReadAllOpenOrdersRequestV2.class))).thenReturn(response);
        when(sapReadAllOpenOrdersDetailsResponse.convert(matchingOpenOrder)).thenReturn(orderData);
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(userService.getCurrentUser()).thenReturn(customer);
        when(customer.getDefaultB2BUnit()).thenReturn(b2bUnitModel);

        OrderData result = sapOrderHistoryFacade.getOpenOrderDetailsForCode(testCode);

        // then
        verify(sapOrderService).readAllOpenOrders(any(ReadAllOpenOrdersRequestV2.class));
        verify(sapReadAllOpenOrdersDetailsResponse).convert(matchingOpenOrder);
        assertThat(result, is(orderData));
    }

    @Test
    public void testGetOrderForCode() {
        // given
        String testCode = "12345";
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        ReadOrderResponseV2 readOrderResponse = mock(ReadOrderResponseV2.class);
        RMAGetOrderItemsResponse rmaGetOrderItemsResponse = mock(RMAGetOrderItemsResponse.class);
        OrderData orderData = mock(OrderData.class);
        OrderModel orderModel = mock(OrderModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(customer);
        when(customer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bUnit.getErpCustomerID()).thenReturn("ERP123");
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(mock(DistSalesOrgModel.class));
        when(sapOrderService.readOrder(any(ReadOrderRequestV2.class))).thenReturn(readOrderResponse);
        when(returnOnlineService.rmaGetOrderItems(any(RMAGetOrderItemsRequest.class))).thenReturn(rmaGetOrderItemsResponse);
        when(sapReadOrderResponseOrderHistoryConverter.convert(any(ReadOrderResponseWraper.class))).thenReturn(orderData);
        when(distOrderService.getOrderForErpCode(anyString())).thenReturn(orderModel);
        when(orderData.getOrderDate()).thenReturn(new Date());
        when(distrelecCMSSiteService.getCurrentSite()).thenReturn(mock(CMSSiteModel.class));
        when(configurationService.getConfiguration()).thenReturn(mock(Configuration.class));

        OrderData result = sapOrderHistoryFacade.getOrderForCode(testCode);

        // then
        verify(sapOrderService).readOrder(any(ReadOrderRequestV2.class));
        verify(returnOnlineService).rmaGetOrderItems(any(RMAGetOrderItemsRequest.class));
        verify(sapReadOrderResponseOrderHistoryConverter).convert(any(ReadOrderResponseWraper.class));
        assertThat(result, is(orderData));
    }

    @Test
    public void testGetOrderForObsolescence() {
        // given
        AbstractOrderModel order = mock(AbstractOrderModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        DistSalesOrgModel salesOrg = mock(DistSalesOrgModel.class);
        ReadOrderResponseV2 readOrderResponse = mock(ReadOrderResponseV2.class);
        RMAGetOrderItemsResponse rmaGetOrderItemsResponse = mock(RMAGetOrderItemsResponse.class);
        OrderData orderData = mock(OrderData.class);
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);

        // when
        when(order.getUser()).thenReturn(customer);
        when(customer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bUnit.getSalesOrg()).thenReturn(salesOrg);
        when(salesOrg.getCode()).thenReturn("SalesOrgCode");
        when(order.getErpOrderCode()).thenReturn("ERP123");
        when(sapOrderService.readOrder(any(ReadOrderRequestV2.class))).thenReturn(readOrderResponse);
        when(returnOnlineService.rmaGetOrderItems(any(RMAGetOrderItemsRequest.class))).thenReturn(rmaGetOrderItemsResponse);
        when(sapReadOrderResponseOrderHistoryConverter.convert(any(ReadOrderResponseWraper.class))).thenReturn(orderData);
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);

        OrderData result = sapOrderHistoryFacade.getOrderForObsolescence(order);

        // then
        verify(sapOrderService).readOrder(any(ReadOrderRequestV2.class));
        verify(returnOnlineService).rmaGetOrderItems(any(RMAGetOrderItemsRequest.class));
        verify(sapReadOrderResponseOrderHistoryConverter).convert(any(ReadOrderResponseWraper.class));
        assertThat(result, is(orderData));
    }

    @Test
    public void testGetOrderHistory() {
        // given
        DistOrderHistoryPageableData pageableData = mock(DistOrderHistoryPageableData.class);
        String[] statuses = new String[] {"status1", "status2"};
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        OrderSearchResponseV2 orderSearchResponse = mock(OrderSearchResponseV2.class);
        RMAGetListForOrderResponse rmaGetListForOrderResponse = mock(RMAGetListForOrderResponse.class);
        OrderSearchRequestV2 orderSearchRequestV2 = mock(OrderSearchRequestV2.class);
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);
        LanguageModel languageModel = mock(LanguageModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(customer);
        when(sapOrderService.searchOrders(any(OrderSearchRequestV2.class))).thenReturn(orderSearchResponse);
        when(returnOnlineService.rmaListForOrder(any(RMAGetListForOrderRequest.class))).thenReturn(rmaGetListForOrderResponse);
        when(customer.getErpContactID()).thenReturn("3091128");
        when(sapSearchOrderRequestConverter.convert(pageableData)).thenReturn(orderSearchRequestV2);
        when(orderSearchResponse.getResultTotalSize()).thenReturn(BigInteger.valueOf(10));
        when(orderSearchResponse.getOrders()).thenReturn(List.of(mock(OrdersSearchLine.class)));
        when(customer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bUnit.getErpCustomerID()).thenReturn("3091128");
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(customer.getSessionLanguage()).thenReturn(languageModel);
        when(languageModel.getIsocode()).thenReturn("DE");

        SearchPageData<OrderHistoryData> result = sapOrderHistoryFacade.getOrderHistory(pageableData, statuses);

        // then
        verify(sapOrderService).searchOrders(any(OrderSearchRequestV2.class));
        verify(returnOnlineService).rmaListForOrder(any(RMAGetListForOrderRequest.class));
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void testGetOrderHistoryById() {
        // given
        String orderId = "12345";
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        OrderSearchResponseV2 orderSearchResponse = mock(OrderSearchResponseV2.class);
        OrderSearchRequestV2 orderSearchRequest = mock(OrderSearchRequestV2.class);

        // when
        when(userService.getCurrentUser()).thenReturn(customer);
        when(sapOrderService.searchOrders(any(OrderSearchRequestV2.class))).thenReturn(orderSearchResponse);
        when(orderSearchResponse.getOrders()).thenReturn(List.of(mock(OrdersSearchLine.class)));
        when(sapSearchOrderRequestConverter.convert(any(DistOrderHistoryPageableData.class))).thenReturn(orderSearchRequest);
        when(customer.getErpContactID()).thenReturn("3091128");

        sapOrderHistoryFacade.getOrderHistoryById(orderId);

        // then
        verify(sapOrderService).searchOrders(orderSearchRequest);
        verify(sapSearchOrderRequestConverter).convert(any(DistOrderHistoryPageableData.class));
    }

    @Test
    public void testIsOrderHistoryEmpty() {
        // given
        String uid = "distrelec.customer@distrelec.com";
        B2BCustomerModel b2bCustomerModel = mock(B2BCustomerModel.class);
        B2BUnitModel b2bUnitModel = mock(B2BUnitModel.class);

        // when
        when(userService.getUserForUID(uid, B2BCustomerModel.class)).thenReturn(b2bCustomerModel);
        when(b2bCustomerModel.getErpContactID()).thenReturn("2343254");
        when(b2bCustomerModel.getDefaultB2BUnit()).thenReturn(b2bUnitModel);

        boolean result = sapOrderHistoryFacade.isOrderHistoryEmpty(uid);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testUpdateProjectNumber() {
        // given
        String orderCode = "34dsfd0w930";
        String workflowCode = "workflowCode";
        String orderReference = "dfdsf1232";

        // when
        boolean result = sapOrderHistoryFacade.updateProjectNumber(orderCode, workflowCode, orderReference);

        // then
        verify(distOrderService).updateProjectNumber(orderCode, workflowCode, orderReference);
        assertThat(result, is(true));
    }
}

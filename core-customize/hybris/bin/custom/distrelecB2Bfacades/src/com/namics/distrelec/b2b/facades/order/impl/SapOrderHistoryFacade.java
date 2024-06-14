/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import com.distrelec.webservice.if15.v1.*;
import com.distrelec.webservice.if19.v1.*;
import com.google.common.base.Preconditions;
import com.namics.distrelec.b2b.core.inout.erp.OrderService;
import com.namics.distrelec.b2b.core.inout.erp.ReturnOnlineService;
import com.namics.distrelec.b2b.core.service.order.DistOrderService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistPaginationData;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.facades.order.OrderHistoryFacade;
import com.namics.distrelec.b2b.facades.search.converter.ReadOrderResponseWraper;
import com.namics.distrelec.b2b.facades.util.AbstractDistComparator;
import com.namics.distrelec.b2b.facades.util.OpenOrderComparator;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;

public class SapOrderHistoryFacade implements OrderHistoryFacade {

    private static final Logger LOG = LogManager.getLogger(SapOrderHistoryFacade.class);

    private static final String RMA_CREATE_VALID_DATE_DURATION = "rma.create.valid.date.duration";

    @Autowired
    private OrderService sapOrderService;

    @Autowired
    private DistOrderService distOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    @Qualifier("sapReadOrderResponseOrderHistoryConverter")
    private Converter<ReadOrderResponseWraper, OrderData> sapReadOrderResponseOrderHistoryConverter;

    @Autowired
    // @Qualifier("sapSearchOrderRequestConverter")
    private Converter<DistOrderHistoryPageableData, OrderSearchRequestV2> sapSearchOrderRequestConverter;

    @Autowired
    private Converter<OrdersSearchLine, OrderHistoryData> sapSearchOrderHistoryConverter;

    @Autowired
    private Converter<OpenOrders, OrderHistoryData> sapReadAllOpenOrdersResponseConverter;

    @Autowired
    private Converter<OpenOrders, OrderData> sapReadAllOpenOrdersDetailsResponse;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistrelecCMSSiteService distrelecCMSSiteService;

    @Autowired
    private ReturnOnlineService returnOnlineService;

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchPageData<OrderHistoryData> getOpenOrders(final DistOrderHistoryPageableData pageableData) {
        // Search open order for the current customer
        final ReadAllOpenOrdersResponseV2 orderSearchResponse = getOpenOrderForCurrentLoggedInCustomer();

        final List<OrderHistoryData> distB2BOrderHistoryDatas = orderSearchResponse == null
                                                                                            ? Collections.emptyList()
                                                                                            : Converters.convertAll(orderSearchResponse.getOpenOrders(),
                                                                                                                    getSapReadAllOpenOrdersResponseConverter());
        final AbstractDistComparator<OrderHistoryData> abstractDistComparator = OpenOrderComparator.getComparators()
                                                                                                   .get(pageableData.getSort());
        if (abstractDistComparator != null) {
            Collections.sort(distB2BOrderHistoryDatas, abstractDistComparator);
        }
        return getPaginatedData(pageableData, distB2BOrderHistoryDatas);
    }

    /**
     * get OpenOrders For Current Logged In Customer by making a soap call
     *
     * @return ReadAllOpenOrdersResponse
     */
    private ReadAllOpenOrdersResponseV2 getOpenOrderForCurrentLoggedInCustomer() {
        final String salesOrg = getDistSalesOrgService().getCurrentSalesOrg().getCode();
        final String erpCustomerID = ((B2BCustomerModel) getUserService().getCurrentUser()).getDefaultB2BUnit()
                                                                                           .getErpCustomerID();
        final ReadAllOpenOrdersRequestV2 readAllOpenOrdersRequest = new ReadAllOpenOrdersRequestV2();
        readAllOpenOrdersRequest.setSalesOrganization(salesOrg);
        readAllOpenOrdersRequest.setCustomerId(erpCustomerID);
        return getSapOrderService().readAllOpenOrders(readAllOpenOrdersRequest);
    }

    /**
     * This logic of Pagination is used among searchOrderHistory and
     * searchOpenOrderHistory
     *
     * @param pageableData
     * @param distB2BOrderHistoryDatas
     * @return {@link SearchPageData}
     */
    private SearchPageData<OrderHistoryData> getPaginatedData(final PageableData pageableData,
                                                              final List<OrderHistoryData> distB2BOrderHistoryDatas) {
        final SearchPageData<OrderHistoryData> searchPageData = new SearchPageData<>();
        final DistPaginationData resultPagination = new DistPaginationData();
        resultPagination.setCurrentPage(pageableData.getCurrentPage());
        final int resultTotalSize = pageableData.getCurrentPage() * pageableData.getPageSize()
                                    + distB2BOrderHistoryDatas.size();
        resultPagination.setNumberOfPages((int) Math.ceil((double) resultTotalSize / pageableData.getPageSize()));
        resultPagination.setPageSize(pageableData.getPageSize());
        resultPagination.setSort(pageableData.getSort());
        resultPagination.setTotalNumberOfResults(resultTotalSize);

        searchPageData.setPagination(resultPagination);

        if (distB2BOrderHistoryDatas.size() > pageableData.getPageSize()) {
            // Since we get one more item, we need to remove it from the list
            distB2BOrderHistoryDatas.remove(distB2BOrderHistoryDatas.size() - 1);
        }

        // convert search order data to result data
        searchPageData.setResults(distB2BOrderHistoryDatas);
        return searchPageData;
    }

    @Override
    public OrderData getOpenOrderDetailsForCode(final String code) {
        final ReadAllOpenOrdersResponseV2 readAllOpenOrdersResponse = getOpenOrderForCurrentLoggedInCustomer();
        final OpenOrders openOrderForCode = getOpenOrderForCode(code, readAllOpenOrdersResponse);
        return openOrderForCode == null ? null : getSapReadAllOpenOrdersDetailsResponse().convert(openOrderForCode);
    }

    /**
     * there can be multiple open-order for a customers hence a matching is needed
     * from orders retrieved against the give code
     *
     * @param code
     *            OpenOrderId
     * @return openOrderForCode matching OpenOrder
     */
    private OpenOrders getOpenOrderForCode(final String code,
                                           final ReadAllOpenOrdersResponseV2 readAllOpenOrdersResponse) {
        if (readAllOpenOrdersResponse == null || readAllOpenOrdersResponse.getOpenOrders() == null) {
            return null;
        }

        return readAllOpenOrdersResponse.getOpenOrders().stream()
                                        .filter(openOrder -> openOrder.getOrderId().equals(code)).findFirst().orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderData getOrderForCode(final String code) {
        // For SAP, a customer can access all orders of the its company
        final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
        final ReadOrderResponseV2 readOrderResponse = getSapOrderDetails(
                                                                         getDistSalesOrgService().getCurrentSalesOrg().getCode(), customer, code);
        OrderData orderData = null;
        if (null != readOrderResponse && null != customer) {
            final RMAGetOrderItemsRequest rmaGetOrderItemsRequest = getRMAOrderItems(customer, readOrderResponse);
            final RMAGetOrderItemsResponse rmaGetOrderItemsResponse = returnOnlineService
                                                                                         .rmaGetOrderItems(rmaGetOrderItemsRequest);
            orderData = getSapReadOrderResponseOrderHistoryConverter()
                                                                      .convert(new ReadOrderResponseWraper(readOrderResponse, customer,
                                                                                                           rmaGetOrderItemsResponse));

            if (orderData != null && orderData.getOrderDate() != null) {
                orderData.setValidForReturn(validateReturnClaimDate(orderData.getOrderDate()));
            }
        }

        return orderData;
    }

    @Override
    public OrderData getOrderForObsolescence(final AbstractOrderModel order) {
        // For SAP, a customer can access all orders of the its company
        final CustomerModel customer = (CustomerModel) order.getUser();
        final ReadOrderResponseV2 readOrderResponse = getSapOrderDetails(
                                                                         ((B2BCustomerModel) customer).getDefaultB2BUnit().getSalesOrg().getCode(),
                                                                         (B2BCustomerModel) customer,
                                                                         order.getErpOrderCode());
        OrderData orderData = null;
        if (null != readOrderResponse) {
            // Call IF-19 interface method to get RMAGetListForOrderRequest
            final RMAGetOrderItemsRequest rmaGetOrderItemsRequest = getRMAOrderItems((B2BCustomerModel) customer,
                                                                                     readOrderResponse);
            final RMAGetOrderItemsResponse rmaGetOrderItemsResponse = returnOnlineService
                                                                                         .rmaGetOrderItems(rmaGetOrderItemsRequest);
            orderData = getSapReadOrderResponseOrderHistoryConverter()
                                                                      .convert(new ReadOrderResponseWraper(readOrderResponse,
                                                                                                           (B2BCustomerModel) customer,
                                                                                                           rmaGetOrderItemsResponse));
        }

        return orderData;
    }

    /**
     * Read order details from SAP
     *
     * @param salesOrganization
     * @param currentUser
     * @param erpOrderId
     * @return the {@code ReadOrderResponse}
     */
    private ReadOrderResponseV2 getSapOrderDetails(final String salesOrganization, final B2BCustomerModel currentUser, final String erpOrderId) {

        final B2BUnitModel currentUnit = currentUser.getDefaultB2BUnit();
        final ReadOrderRequestV2 readOrderRequest = new ReadOrderRequestV2();
        final AbstractOrderModel webshopOrder = getDistOrderService().getOrderForErpCode(erpOrderId);

        String orderIdParameter;
        boolean isWebshopOrder = false;

        if (webshopOrder == null || StringUtils.isNotEmpty(erpOrderId)) {
            orderIdParameter = erpOrderId;
        } else {
            isWebshopOrder = true;
            orderIdParameter = webshopOrder.getCode();
        }

        readOrderRequest.setOrderId(orderIdParameter);
        readOrderRequest.setWebshopOrderFlag(isWebshopOrder);
        readOrderRequest.setSalesOrganization(salesOrganization);
        readOrderRequest.setCustomerId(currentUnit.getErpCustomerID());

        return getSapOrderService().readOrder(readOrderRequest);
    }

    @Deprecated
    @Override
    public List<OrderData> getOrderHistory() {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Deprecated
    @Override
    public List<OrderData> getOrderHistory(final String uid) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Deprecated
    @Override
    public List<OrderData> getOrderHistory(final String[] status) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchPageData<OrderHistoryData> getOrderHistory(final DistOrderHistoryPageableData pageableData,
                                                            final String[] statuses) {
        // Search orders for the current customer

        final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();

        final OrderSearchResponseV2 orderSearchResponse = searchOrders(pageableData, customer);
        List<OrderHistoryData> distB2BOrderHistoryDatas = (orderSearchResponse == null)
                                                                                        ? Collections.emptyList()
                                                                                        : Converters.convertAll(orderSearchResponse.getOrders(),
                                                                                                                getSapSearchOrderHistoryConverter());
        if (orderSearchResponse != null && CollectionUtils.isNotEmpty(orderSearchResponse.getOrders())) {
            // Get the request for RMA List items for order
            final RMAGetListForOrderRequest rmaGetListForOrderRequest = getRMAListForOrder(customer,
                                                                                           orderSearchResponse);

            // Create RMAGetListForOrderResponse from rmaGetListForOrderRequest to get the
            // RMA details for order from SAP.
            final RMAGetListForOrderResponse rmaGetListForOrderResponse = getReturnOnlineService()
                                                                                                  .rmaListForOrder(rmaGetListForOrderRequest);

            // Set the RMA status for order in order History Data
            if (null != rmaGetListForOrderResponse && null != rmaGetListForOrderResponse.getOrders()) {
                distB2BOrderHistoryDatas = getRMAStatusforOrder(rmaGetListForOrderResponse.getOrders().getOrder(),
                                                                distB2BOrderHistoryDatas);
            }
        }

        // applying pagination logic
        final SearchPageData<OrderHistoryData> searchPageData = new SearchPageData<>();
        final DistPaginationData resultPagination = new DistPaginationData();
        resultPagination.setCurrentPage(pageableData.getCurrentPage());
        final int resultTotalSize = orderSearchResponse == null ? 0
                                                                : orderSearchResponse.getResultTotalSize().intValue();
        resultPagination.setNumberOfPages((int) Math.ceil((double) resultTotalSize / pageableData.getPageSize()));
        resultPagination.setPageSize(pageableData.getPageSize());
        resultPagination.setSort(pageableData.getSort());
        resultPagination.setTotalNumberOfResults(resultTotalSize);

        searchPageData.setPagination(resultPagination);

        // convert search order data to result data
        searchPageData.setResults(distB2BOrderHistoryDatas);

        return searchPageData;

    }

    @Override
    public OrderHistoryData getOrderHistoryById(final String orderId) {
        final DistOrderHistoryPageableData pageableData = new DistOrderHistoryPageableData();
        pageableData.setOrderNumber(orderId);

        final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();

        final OrderSearchResponseV2 orderSearchResponse = searchOrders(pageableData, customer);
        final List<OrderHistoryData> distB2BOrderHistoryDatas = orderSearchResponse == null
                                                                                            ? Collections.<OrderHistoryData> emptyList()
                                                                                            : Converters.convertAll(orderSearchResponse.getOrders(),
                                                                                                                    getSapSearchOrderHistoryConverter());

        if (CollectionUtils.isNotEmpty(distB2BOrderHistoryDatas)) {
            return distB2BOrderHistoryDatas.get(0);
        }

        return null;
    }

    /**
     * This method receives the list of RMA status form SAP and add that to order
     * history Data to be displayed on web-shop.
     *
     * @param orderList
     * @param distB2BOrderHistoryDataList
     * @return List<OrderHistoryData>
     */
    private List<OrderHistoryData> getRMAStatusforOrder(final List<OrdersList2> orderList,
                                                        final List<OrderHistoryData> distB2BOrderHistoryDataList) {

        for (final OrdersList2 order : orderList) {
            if (null != order.getRmas() && order.getRmas().getRma().size() > 0) {
                final OrderHistoryData orderHistoryData = distB2BOrderHistoryDataList.stream()
                                                                                     .filter(orderData -> orderData.getCode().equals(order.getOrderId()))
                                                                                     .findFirst().orElse(null);

                // Set the RMA Status in Order History Data
                if (null != orderHistoryData) {
                    orderHistoryData.setRmaStatus(true);
                }

            }
        }

        return distB2BOrderHistoryDataList;
    }

    @Override
    public boolean isOrderHistoryEmpty(final String uid) {
        final DistOrderHistoryPageableData pageableData = new DistOrderHistoryPageableData(0, 1, null, null);
        final String[] statuses = null; // FIXME once statuses are supported
        final SearchPageData<OrderHistoryData> orderHistory = getOrderHistory(uid, pageableData, statuses);
        return CollectionUtils.isEmpty(orderHistory.getResults());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchPageData<OrderHistoryData> getOrderHistory(final String b2bCustomerModelUid,
                                                            final DistOrderHistoryPageableData pageableData, final String[] statuses) {

        // Search orders for the given customer
        final B2BCustomerModel b2bCustomerModel = getUserService().getUserForUID(b2bCustomerModelUid,
                                                                                 B2BCustomerModel.class);
        return getOrderHistory(b2bCustomerModel, pageableData, statuses);
    }

    public SearchPageData<OrderHistoryData> getOrderHistory(final B2BCustomerModel b2bCustomerModel,
                                                            final DistOrderHistoryPageableData pageableData, final String[] statuses) {
        Assert.notNull(b2bCustomerModel);
        final OrderSearchResponseV2 orderSearchResponse = searchOrders(pageableData, b2bCustomerModel);
        final List<OrderHistoryData> distB2BOrderHistoryDatas = orderSearchResponse == null
                                                                                            ? Collections.<OrderHistoryData> emptyList()
                                                                                            : Converters.convertAll(orderSearchResponse.getOrders(),
                                                                                                                    getSapSearchOrderHistoryConverter());

        // applying pagination logic
        final SearchPageData<OrderHistoryData> searchPageData = new SearchPageData<>();
        final DistPaginationData resultPagination = new DistPaginationData();
        resultPagination.setCurrentPage(pageableData.getCurrentPage());
        final int resultTotalSize = orderSearchResponse == null ? 0
                                                                : orderSearchResponse.getResultTotalSize().intValue();
        resultPagination.setNumberOfPages((int) Math.ceil((double) resultTotalSize / pageableData.getPageSize()));
        resultPagination.setPageSize(pageableData.getPageSize());
        resultPagination.setSort(pageableData.getSort());
        resultPagination.setTotalNumberOfResults(resultTotalSize);

        searchPageData.setPagination(resultPagination);

        // convert search order data to result data
        searchPageData.setResults(distB2BOrderHistoryDatas);
        if (LOG.isInfoEnabled()) {
            final B2BUnitModel defaultB2BUnit = b2bCustomerModel.getDefaultB2BUnit();
            Assert.notNull(defaultB2BUnit);
            LOG.info(
                     "getOrderHistory for customer with uid: {} and contactId: {}, unit with uid: {} and customerId: {} returns {} results",
                     b2bCustomerModel.getUid(), b2bCustomerModel.getErpContactID(), defaultB2BUnit.getUid(),
                     defaultB2BUnit.getErpCustomerID(), searchPageData.getResults().size());
        }
        return searchPageData;
    }

    private OrderSearchResponseV2 searchOrders(final DistOrderHistoryPageableData pageableData,
                                               final B2BCustomerModel b2BCustomerModel) {
        validateParameterNotNull(pageableData, "pageableData must not be null");
        validateParameterNotNull(b2BCustomerModel, "b2BCustomerModel must not be null");
        Preconditions.checkArgument(StringUtils.isNotBlank(b2BCustomerModel.getErpContactID()),
                                    String.format("customer: %s cannot have blank erpContactId", b2BCustomerModel.getUid()));

        if (null == pageableData.getContactId()) {
            pageableData.setContactId(b2BCustomerModel.getErpContactID());
        }

        final OrderSearchRequestV2 orderSearchRequest = getSapSearchOrderRequestConverter().convert(pageableData);
        return getSapOrderService().searchOrders(orderSearchRequest);
    }

    /**
     * This method is used to create RMAGetOrderItemsRequest, used to get the RMA
     * status for Order Details Page.
     *
     * @param customer
     * @param readOrderResponse
     * @return RMAGetOrderItemsRequest
     */
    private RMAGetOrderItemsRequest getRMAOrderItems(final B2BCustomerModel customer,
                                                     final ReadOrderResponseV2 readOrderResponse) {
        final RMAGetOrderItemsRequest rmaGetOrderItemsRequest = new RMAGetOrderItemsRequest();

        rmaGetOrderItemsRequest.setCustomerId(customer.getDefaultB2BUnit().getErpCustomerID());
        rmaGetOrderItemsRequest.setOrderId(readOrderResponse.getOrderId());
        rmaGetOrderItemsRequest.setSalesOrganization(getDistSalesOrgService().getCurrentSalesOrg().getCode());
        rmaGetOrderItemsRequest.setSessionLanguage(Locale.ENGLISH.getLanguage().toUpperCase());

        return rmaGetOrderItemsRequest;
    }

    /**
     * This method is used to create RMAGetListForOrderRequest, used to get the RMA
     * status for Order History List page.
     *
     * @param customer
     * @param orderSearchResponse
     * @return RMAGetListForOrderRequest
     */
    private RMAGetListForOrderRequest getRMAListForOrder(final B2BCustomerModel customer,
                                                         final OrderSearchResponseV2 orderSearchResponse) {

        final RMAGetListForOrderRequest rmaGetListForOrderRequest = new RMAGetListForOrderRequest();
        rmaGetListForOrderRequest.setCustomerId(customer.getDefaultB2BUnit().getErpCustomerID());
        rmaGetListForOrderRequest.setSalesOrganization(getDistSalesOrgService().getCurrentSalesOrg().getCode());
        rmaGetListForOrderRequest.setSessionLanguage(customer.getSessionLanguage().getIsocode().toUpperCase());

        final OrderList orderList = new OrderList();
        final List<OrdersSearchLine> ordersSearchLineList = null != orderSearchResponse
                                                                                        ? orderSearchResponse.getOrders()
                                                                                        : Collections.EMPTY_LIST;
        if (null != ordersSearchLineList && ordersSearchLineList.size() > 0) {
            for (final OrdersSearchLine ordersSearchLine : ordersSearchLineList) {
                orderList.getOrderId().add(ordersSearchLine.getOrderId());
            }
        }
        rmaGetListForOrderRequest.setOrders(orderList);
        return rmaGetListForOrderRequest;
    }

    /**
     * This methods validate if the return request raised date is valid within valid
     * duration.
     *
     * @param orderDate
     * @return boolean
     * @throws ParseException
     */
    private boolean validateReturnClaimDate(final Date orderDate) {

        boolean isValidReturnClaim = false;
        final LocalDate currentDate = LocalDate.now();

        final int duration = getRmaDuration();
        final LocalDate validDate = currentDate.minusDays(duration);
        final Date returnValidDate = Date.from(validDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        if (orderDate.after(returnValidDate)) {
            isValidReturnClaim = true;
        }
        return isValidReturnClaim;
    }

    private int getRmaDuration() {
        final CMSSiteModel cmsSite = getDistrelecCMSSiteService().getCurrentSite();
        final Configuration config = configurationService.getConfiguration();

        // check if RMA period is defined on site or global level
        final String siteDefinedConfig = RMA_CREATE_VALID_DATE_DURATION + "." + cmsSite.getUid();
        final int duration;
        if (config.containsKey(siteDefinedConfig)) {
            duration = config.getInt(siteDefinedConfig);
        } else {
            duration = config.getInt(RMA_CREATE_VALID_DATE_DURATION, 180);
        }
        return duration;
    }

    @Override
    public boolean updateProjectNumber(String orderCode, String workflowCode, String orderReference) {
        try {
            distOrderService.updateProjectNumber(orderCode, workflowCode, orderReference);
        } catch (UnknownIdentifierException ex) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /* Getters & Setters */

    public OrderService getSapOrderService() {
        return sapOrderService;
    }

    public void setSapOrderService(final OrderService sapOrderService) {
        this.sapOrderService = sapOrderService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public Converter<ReadOrderResponseWraper, OrderData> getSapReadOrderResponseOrderHistoryConverter() {
        return sapReadOrderResponseOrderHistoryConverter;
    }

    public void setSapReadOrderResponseOrderHistoryConverter(
                                                             final Converter<ReadOrderResponseWraper, OrderData> sapReadOrderResponseOrderHistoryConverter) {
        this.sapReadOrderResponseOrderHistoryConverter = sapReadOrderResponseOrderHistoryConverter;
    }

    public Converter<DistOrderHistoryPageableData, OrderSearchRequestV2> getSapSearchOrderRequestConverter() {
        return sapSearchOrderRequestConverter;
    }

    public void setSapSearchOrderRequestConverter(
                                                  final Converter<DistOrderHistoryPageableData, OrderSearchRequestV2> sapSearchOrderRequestConverter) {
        this.sapSearchOrderRequestConverter = sapSearchOrderRequestConverter;
    }

    public Converter<OrdersSearchLine, OrderHistoryData> getSapSearchOrderHistoryConverter() {
        return sapSearchOrderHistoryConverter;
    }

    public void setSapSearchOrderHistoryConverter(
                                                  final Converter<OrdersSearchLine, OrderHistoryData> sapSearchOrderHistoryConverter) {
        this.sapSearchOrderHistoryConverter = sapSearchOrderHistoryConverter;
    }

    public Converter<OpenOrders, OrderHistoryData> getSapReadAllOpenOrdersResponseConverter() {
        return sapReadAllOpenOrdersResponseConverter;
    }

    public void setSapReadAllOpenOrdersResponseConverter(
                                                         final Converter<OpenOrders, OrderHistoryData> sapReadAllOpenOrdersResponseConverter) {
        this.sapReadAllOpenOrdersResponseConverter = sapReadAllOpenOrdersResponseConverter;
    }

    public Converter<OpenOrders, OrderData> getSapReadAllOpenOrdersDetailsResponse() {
        return sapReadAllOpenOrdersDetailsResponse;
    }

    public void setSapReadAllOpenOrdersDetailsResponse(
                                                       final Converter<OpenOrders, OrderData> sapReadAllOpenOrdersDetailsResponse) {
        this.sapReadAllOpenOrdersDetailsResponse = sapReadAllOpenOrdersDetailsResponse;
    }

    public DistrelecCMSSiteService getDistrelecCMSSiteService() {
        return distrelecCMSSiteService;
    }

    public void setDistrelecCMSSiteService(final DistrelecCMSSiteService distrelecCMSSiteService) {
        this.distrelecCMSSiteService = distrelecCMSSiteService;
    }

    public ReturnOnlineService getReturnOnlineService() {
        return returnOnlineService;
    }

    public void setReturnOnlineService(final ReturnOnlineService returnOnlineService) {
        this.returnOnlineService = returnOnlineService;
    }

    @Override
    public List<OrderHistoryData> getOrdersForStatuses(final DistOrderHistoryPageableData pageableData) {
        final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();

        final OrderSearchResponseV2 orderSearchResponse = searchOrders(pageableData, customer);
        final List<OrderHistoryData> distB2BOrderHistoryDatas = orderSearchResponse == null
                                                                                            ? Collections.<OrderHistoryData> emptyList()
                                                                                            : Converters.convertAll(orderSearchResponse.getOrders(),
                                                                                                                    getSapSearchOrderHistoryConverter());
        return distB2BOrderHistoryDatas;

    }

    public DistOrderService getDistOrderService() {
        return distOrderService;
    }

    public void setDistOrderService(final DistOrderService distOrderService) {
        this.distOrderService = distOrderService;
    }
}

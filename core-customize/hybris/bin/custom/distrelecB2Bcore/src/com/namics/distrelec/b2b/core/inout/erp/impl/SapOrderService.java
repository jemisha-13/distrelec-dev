/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.distrelec.webservice.if11.v3.OpenOrderCalculationRequest;
import com.distrelec.webservice.if11.v3.OpenOrderCalculationResponse;
import com.distrelec.webservice.if11.v3.P1FaultMessage;
import com.distrelec.webservice.if11.v3.SIHybrisIF11V1Out;
import com.distrelec.webservice.if15.v1.OpenOrders;
import com.distrelec.webservice.if15.v1.OrderSearchRequestV2;
import com.distrelec.webservice.if15.v1.OrderSearchResponseV2;
import com.distrelec.webservice.if15.v1.P2FaultMessage;
import com.distrelec.webservice.if15.v1.ReadAllOpenOrdersRequestV2;
import com.distrelec.webservice.if15.v1.ReadAllOpenOrdersResponseV2;
import com.distrelec.webservice.if15.v1.ReadOrderRequestV2;
import com.distrelec.webservice.if15.v1.ReadOrderResponseV2;
import com.distrelec.webservice.if15.v1.SIHybrisIF15Out;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.OrderService;
import com.namics.distrelec.b2b.core.inout.erp.converters.request.OpenOrderCalculationRequestFromExtModelConverter;
import com.namics.distrelec.b2b.core.inout.erp.exception.ErpCommunicationException;
import com.namics.distrelec.b2b.core.inout.erp.impl.cache.SapReadAllOpenOrdersRequestCacheKey;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapErrorLogHelper;
import com.namics.distrelec.b2b.core.service.order.model.ErpOpenOrderExtModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

/**
 * @author Neeraj Shandilya, Elfa Distrelec AB
 * @since Namics Extensions 2.0
 * 
 */
public class SapOrderService extends AbstractSapService implements OrderService {

    private static final Logger LOG = LogManager.getLogger(SapOrderService.class);

    // injected by spring
    private SIHybrisV1Out webServiceClient;

    private SIHybrisIF15Out webServiceClientIF15;
    private SIHybrisIF11V1Out webServiceIF11Client;
    private UserService userService;
    private DistSalesOrgService distSalesOrgService;
    private Converter<OpenOrders, ErpOpenOrderExtModel> erpOpenOrderExtModelConvertor;
    private OpenOrderCalculationRequestFromExtModelConverter openOrderCalculationRequestFromExtModelConverter;

    /**
     * Create a new instance of {@code SapOrderService}
     */
    public SapOrderService() {
        super(DistConstants.CacheName.ORDER_SEARCH);
    }

    @Override
    public ReadOrderResponseV2 readOrder(final ReadOrderRequestV2 readOrderRequest) {
        ReadOrderResponseV2 readOrderResponse = getOrderResponseFromCache(readOrderRequest);

        if (readOrderResponse == null) {
            readOrderResponse = fetchUncachedreadOrder(readOrderRequest);
        }
        return readOrderResponse;
    }

    @Override
    public OrderSearchResponseV2 searchOrders(final OrderSearchRequestV2 orderSearchRequest) {
        OrderSearchResponseV2 orderSearchResponse = getOrderSearchResponseFromCache(orderSearchRequest);

        if (orderSearchResponse == null) {
            orderSearchResponse = fetchUncachedorderSearchResponse(orderSearchRequest);
        }

        return orderSearchResponse;
    }

    @Override
    public ReadAllOpenOrdersResponseV2 readAllOpenOrders(final ReadAllOpenOrdersRequestV2 readAllOpenOrdersRequest) {
        ReadAllOpenOrdersResponseV2 readAllOpenOrdersResponse = getAllOpenOrdersResponseFromCache(readAllOpenOrdersRequest);

        if (readAllOpenOrdersResponse == null) {
            readAllOpenOrdersResponse = fetchUncachedreadAllOpenOrders(readAllOpenOrdersRequest);
        }

        return readAllOpenOrdersResponse;
    }

    @Override
    public List<ErpOpenOrderExtModel> getOpenOrdersForCurrentCustomer() throws ErpCommunicationException {

        final String salesOrg = getDistSalesOrgService().getCurrentSalesOrg().getCode();
        final String erpCustomerID = ((B2BCustomerModel) getUserService().getCurrentUser()).getDefaultB2BUnit().getErpCustomerID();

        final ReadAllOpenOrdersRequestV2 request = new ReadAllOpenOrdersRequestV2();

        request.setCustomerId(erpCustomerID);
        request.setSalesOrganization(salesOrg);

        final ReadAllOpenOrdersResponseV2 readAllOpenOrders = readAllOpenOrders(request);
        return readAllOpenOrders == null ? Collections.<ErpOpenOrderExtModel> emptyList()
                : Converters.convertAll(readAllOpenOrders.getOpenOrders(), getErpOpenOrderExtModelConvertor());

    }

    @Override
    public ErpOpenOrderExtModel getOpenOrderForErpOrderCode(String erpOpenOrderCode) throws ErpCommunicationException {
        final List<ErpOpenOrderExtModel> openOrdersForCurrentCustomer = getOpenOrdersForCurrentCustomer();
        for (final ErpOpenOrderExtModel openOrder : openOrdersForCurrentCustomer) {
            if (openOrder.getErpOrderId().equals(erpOpenOrderCode)) {
                return openOrder;
            }
        }

        return null;
    }

    @Override
    public void modifyOpenOrderInErp(final ErpOpenOrderExtModel order) throws ErpCommunicationException {
        ServicesUtil.validateParameterNotNull(order, "The order must not be null");

        final OpenOrderCalculationRequest request = openOrderCalculationRequestFromExtModelConverter.convert(order);

        // explicit place the order.
        request.setSimulateOrder(false);
        final OpenOrderCalculationResponse response = executeOpenOrderCalculationRequest(request);
        if (null == response) {
            throw new ErpCommunicationException("Could not get order simulation from SAP PI");
        }
    }

    /*
     * fetch order List from SAP PI, add them to the cache.
     */
    private OrderSearchResponseV2 fetchUncachedorderSearchResponse(final OrderSearchRequestV2 orderSearchRequest) {
        final OrderSearchResponseV2 orderSearchResponse = executeSOAPRequestForOrderSearch(orderSearchRequest);
        if (null != orderSearchResponse && getCache() != null) {
            putIntoCache(orderSearchRequest, orderSearchResponse);
        }

        return orderSearchResponse;
    }

    /*
     * fetch order details from cache.
     */
    private OrderSearchResponseV2 getOrderSearchResponseFromCache(final OrderSearchRequestV2 orderSearchRequest) {
        return getFromCache(orderSearchRequest, OrderSearchResponseV2.class);
    }

    /*
     * fetch order List by making a SOAP call to if15SearchOrders
     */
    private OrderSearchResponseV2 executeSOAPRequestForOrderSearch(final OrderSearchRequestV2 orderSearchRequest) {
        OrderSearchResponseV2 orderSearchResponse = null;
        final long startTime = new Date().getTime();
        try {
            orderSearchResponse = webServiceClientIF15.if15SearchOrders(orderSearchRequest);

        } catch (P2FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if15SearchOrders", faultMessage);
        } catch (WebServiceException wsEx) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if15SearchOrders", wsEx);
        }
        final long endTime = new Date().getTime();
        LOG.debug("Call to SAP PI IF-15 Search Order took " + (endTime - startTime) + "ms");
        return orderSearchResponse;
    }

    /*
     * fetch order details from cache.
     */
    private ReadOrderResponseV2 getOrderResponseFromCache(final ReadOrderRequestV2 readOrderRequest) {
        return getFromCache(readOrderRequest, ReadOrderResponseV2.class);
    }

    /*
     * fetch order details from SAP PI, add them to the cache.
     */
    private ReadOrderResponseV2 fetchUncachedreadOrder(final ReadOrderRequestV2 readOrderRequest) {
        // Make a soap call
        final ReadOrderResponseV2 readOrderResponse = executeSOAPRequestForreadOrder(readOrderRequest);
        if (null != readOrderResponse && getCache() != null) {
            putIntoCache(readOrderRequest, readOrderResponse);
        }
        return readOrderResponse;
    }

    /*
     * fetch order details by making a SOAP call to if15ReadOrder
     */
    private ReadOrderResponseV2 executeSOAPRequestForreadOrder(final ReadOrderRequestV2 readOrderRequest) {
        ReadOrderResponseV2 readOrderResponse = null;
        final long startTime = new Date().getTime();
        try {
            readOrderResponse = webServiceClientIF15.if15ReadOrder(readOrderRequest);
        } catch (P2FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if15ReadOrder", faultMessage);
        } catch (WebServiceException wsEx) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if15ReadOrder", wsEx);
        }
        final long endTime = new Date().getTime();
        LOG.debug("Call to SAP PI IF-15 Read Order took " + (endTime - startTime) + "ms");
        return readOrderResponse;
    }

    private OpenOrderCalculationResponse executeOpenOrderCalculationRequest(final OpenOrderCalculationRequest request){
        OpenOrderCalculationResponse openOrderCalculationResponse = null;
        final long startTime = new Date().getTime();
        try {
            openOrderCalculationResponse = getWebServiceIF11Client().if11V1OpenOrderCalculation(request);
        } catch (P1FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if11OpenOrderCalculation", faultMessage);
        } catch (WebServiceException webServiceException) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if11OpenOrderCalculation", webServiceException);
        }
        final long endTime = new Date().getTime();
        LOG.debug("Call to SAP PI IF-11 Open Order Calculation took " + (endTime - startTime) + "ms");
        return openOrderCalculationResponse;
    }

    private ReadAllOpenOrdersResponseV2 fetchUncachedreadAllOpenOrders(final ReadAllOpenOrdersRequestV2 readAllOpenOrdersRequest) {
        // Make a soap call
        final ReadAllOpenOrdersResponseV2 readAllOpenOrdersResponse = executeSOAPRequestForReadAllOpenOrdersResponse(readAllOpenOrdersRequest);

        if (null != readAllOpenOrdersResponse && getCache() != null) {
            putIntoCache(new SapReadAllOpenOrdersRequestCacheKey(readAllOpenOrdersRequest.getSalesOrganization(), readAllOpenOrdersRequest.getCustomerId()),
                    readAllOpenOrdersResponse);
        }

        return readAllOpenOrdersResponse;
    }

    /*
     * fetch AllOpenOrders by making a SOAP call to if15ReadAllOpenOrders
     */
    private com.distrelec.webservice.if15.v1.ReadAllOpenOrdersResponseV2 executeSOAPRequestForReadAllOpenOrdersResponse(
            final com.distrelec.webservice.if15.v1.ReadAllOpenOrdersRequestV2 readAllOpenOrdersRequest) {
        boolean isEmptyResult = false;
        com.distrelec.webservice.if15.v1.ReadAllOpenOrdersResponseV2 readAllOpenOrdersResponse = null;
        final long startTime = new Date().getTime();
        try {
            readAllOpenOrdersResponse = webServiceClientIF15.if15ReadAllOpenOrders(readAllOpenOrdersRequest);
        } catch (P2FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if15ReadAllOpenOrders", faultMessage);
            if ("00".equals(faultMessage.getFaultInfo().getFaultId())) {
                isEmptyResult = true;
            }
        } catch (WebServiceException wsEx) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if15ReadAllOpenOrders", wsEx);
        }

        if (isEmptyResult) {
            // create empty response
            readAllOpenOrdersResponse = new ReadAllOpenOrdersResponseV2();
            readAllOpenOrdersResponse.getOpenOrders().addAll(new ArrayList<OpenOrders>());
        }

        if (readAllOpenOrdersResponse == null) {
            throw new ErpCommunicationException("null response coming from sap");
        }

        final long endTime = new Date().getTime();
        LOG.debug("Call to SAP PI IF-15 Read All Open Orders took " + (endTime - startTime) + "ms");
        return readAllOpenOrdersResponse;
    }

    private ReadAllOpenOrdersResponseV2 getAllOpenOrdersResponseFromCache(final ReadAllOpenOrdersRequestV2 request) {
        return getFromCache(new SapReadAllOpenOrdersRequestCacheKey(request.getSalesOrganization(), request.getCustomerId()), ReadAllOpenOrdersResponseV2.class);
    }

    // Spring bean injection
    public SIHybrisV1Out getWebServiceClient() {
        return webServiceClient;
    }

    @Required
    public void setWebServiceClient(final SIHybrisV1Out webServiceClient) {
        this.webServiceClient = webServiceClient;
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

    public Converter<OpenOrders, ErpOpenOrderExtModel> getErpOpenOrderExtModelConvertor() {
        return erpOpenOrderExtModelConvertor;
    }

    public void setErpOpenOrderExtModelConvertor(Converter<OpenOrders, ErpOpenOrderExtModel> erpOpenOrderExtModelConvertor) {
        this.erpOpenOrderExtModelConvertor = erpOpenOrderExtModelConvertor;
    }

    public OpenOrderCalculationRequestFromExtModelConverter getOpenOrderCalculationRequestFromExtModelConverter() {
        return openOrderCalculationRequestFromExtModelConverter;
    }

    public void setOpenOrderCalculationRequestFromExtModelConverter(
            final OpenOrderCalculationRequestFromExtModelConverter openOrderCalculationRequestFromExtModelConverter) {
        this.openOrderCalculationRequestFromExtModelConverter = openOrderCalculationRequestFromExtModelConverter;
    }

    public SIHybrisIF15Out getWebServiceClientIF15() {
        return webServiceClientIF15;
    }

    public void setWebServiceClientIF15(SIHybrisIF15Out webServiceClientIF15) {
        this.webServiceClientIF15 = webServiceClientIF15;
    }

    public SIHybrisIF11V1Out getWebServiceIF11Client() {
        return webServiceIF11Client;
    }

    public void setWebServiceIF11Client(SIHybrisIF11V1Out webServiceIF11Client) {
        this.webServiceIF11Client = webServiceIF11Client;
    }

}

/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp;

import java.util.List;

import com.distrelec.webservice.if15.v1.OrderSearchRequestV2;
import com.distrelec.webservice.if15.v1.OrderSearchResponseV2;
import com.distrelec.webservice.if15.v1.ReadAllOpenOrdersRequestV2;
import com.distrelec.webservice.if15.v1.ReadAllOpenOrdersResponseV2;
import com.distrelec.webservice.if15.v1.ReadOrderRequestV2;
import com.distrelec.webservice.if15.v1.ReadOrderResponseV2;
import com.namics.distrelec.b2b.core.inout.erp.exception.ErpCommunicationException;
import com.namics.distrelec.b2b.core.service.order.model.ErpOpenOrderExtModel;

/**
 * @author Neeraj Shandilya, Elfa Distrelec AB
 * @since Namics Extensions 1.0
 * 
 */
public interface OrderService {

    /**
     * This method is used to fetch the details of an individual order
     * 
     * @param ReadOrderRequest
     * @return ReadOrderResponse
     */
    public ReadOrderResponseV2 readOrder(ReadOrderRequestV2 readOrderRequest);

    /**
     * This method is use to fetch the list of orders based on the filter criteria passed as a OrderSearchRequest parameter
     * 
     * @param OrderSearchRequest
     * @return OrderSearchResponse
     */
    public OrderSearchResponseV2 searchOrders(OrderSearchRequestV2 orderSearchRequest);

    /**
     * This method is used to fatch the list of open orders for a given customerId
     * 
     * @param readAllOpenOrdersRequest
     * @return
     */
    public ReadAllOpenOrdersResponseV2 readAllOpenOrders(ReadAllOpenOrdersRequestV2 readAllOpenOrdersRequest);

    /**
     * This method is used in checkout to get the current open orders in ERP for current Company/B2BUnit (erp customer).
     * 
     * @return List<ErpOpenOrderExtModel>
     */
    public List<ErpOpenOrderExtModel> getOpenOrdersForCurrentCustomer();

    /**
     * This method is used in checkout to get a specific open order in ERP for current Company/B2BUnit (erp customer).
     * 
     * @param erpOpenOrderCode
     *            (the Company/B2BUnit erp id)
     * @return ErpOpenOrderExtModel
     */
    public ErpOpenOrderExtModel getOpenOrderForErpOrderCode(final String erpOpenOrderCode);

    /**
     * Save the open order in ERP<br>
     * The information that can be changed in ERP are only the following (DISTRELEC-3532): <br>
     * <ul>
     * <li>Close Date</li>
     * <li>Add items (but only through cart checkout)</li>
     * <li>Shipping Address</li>
     * <li>Order Reference Header Level</li>
     * </ul>
     * 
     * @param order
     */
    public void modifyOpenOrderInErp(final ErpOpenOrderExtModel order) throws ErpCommunicationException;
}

/*
 * Copyright 2018-2022 Datwyler IT Services. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp;

import com.distrelec.webservice.if19.v1.RMACreateRequest;
import com.distrelec.webservice.if19.v1.RMACreateResponse;
import com.distrelec.webservice.if19.v1.RMAGetListForOrderRequest;
import com.distrelec.webservice.if19.v1.RMAGetListForOrderResponse;
import com.distrelec.webservice.if19.v1.RMAGetOrderItemsRequest;
import com.distrelec.webservice.if19.v1.RMAGetOrderItemsResponse;

/**
 * {@code RMAService}
 * <p>
 * Online returns service is used to generate returns online, view returns status and list online returns by for specific customer.
 * </p>
 * 
 * @author <a href="abhinay.jadhav@datwyler.com">Abhinay Jadhav</a>, Distrelec
 * @since Distrelec 7.3
 */
public interface ReturnOnlineService {

    /**
     * This method is used to create returns online for an individual order
     * 
     * @param rmaCreateRequest
     * @return RMACreateResponse
     */
    public RMACreateResponse rmaCreate(RMACreateRequest rmaCreateRequest);
    
    /**
     * This method is used to return details at the order details of an individual order
     * 
     * @param rmaGetOrderItemsRequest
     * @return RMAGetOrderItemsResponse
     */
    public RMAGetOrderItemsResponse rmaGetOrderItems(RMAGetOrderItemsRequest rmaGetOrderItemsRequest);
    
    
    /**
     * This method is used to fetch the return details of all orders
     * 
     * @param rmaGetListForOrderRequest
     * @return RMAGetListForOrderResponse
     */
    public RMAGetListForOrderResponse rmaListForOrder(RMAGetListForOrderRequest rmaGetListForOrderRequest);

}

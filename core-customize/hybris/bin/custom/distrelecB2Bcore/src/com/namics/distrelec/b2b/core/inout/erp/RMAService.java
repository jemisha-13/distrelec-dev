/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp;

import java.util.Date;
import java.util.List;

import com.namics.distrelec.b2b.core.model.DistCodelistModel;
import com.namics.distrelec.b2b.core.rma.ReturnRequestData;

import de.hybris.platform.core.model.order.OrderModel;

/**
 * {@code RMAService}
 * <p>
 * The return request service is used to return some products ordered by a customer. This service allows also to retrieve the current
 * customer return requests and a specified request by its code.
 * </p>
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Namics Extensions 1.0
 */
public interface RMAService {

    /**
     * Create a new RMA object and store it in the database for the current customer.
     * 
     * @param returnRequestData
     *            the data object
     * @return the RMA object code
     */
    String createReturnRequest(ReturnRequestData returnRequestData);

    /**
     * Create a new RMA objects and store them in the database for the current customer.
     * 
     * @param returnRequestDatas
     *            the list of data object
     * @return the list of RMA object codes
     */
    List<String> createReturnRequests(List<ReturnRequestData> returnRequestDatas);

    /**
     * Look for the list of return requests of the current user
     * 
     * @return the list of return requests of the current user
     */
    List<Object> getReturnRequests();

    /**
     * Look for the list of return requests of the current user related to the specified order
     * 
     * @param order
     *            the order
     * @return the list of return requests belonging to the specified order
     */
    List<Object> getReturnRequestsByOrder(OrderModel order);

    /**
     * Retrieve the RMA object having the specified code
     * 
     * @param rmaCode
     *            the RMA code
     * @return the RMA object having the specified code
     */
    Object getReturnRequest(String rmaCode);

    /**
     * Retrieve all RMA objects that are created after the specified date
     * 
     * @param fromDate
     *            the start date
     * @return the list of RMA objects created after {@code fromDate}
     */
    List<Object> getReturnRequestsFromDate(final Date fromDate);

    /**
     * Returns a list of return reasons.
     * 
     * @return a list of return reasons.
     */
    List<? extends DistCodelistModel> getAllReturnRequestReason();

    /**
     * Returns the return reason for the given code.
     * 
     * @return the return reason for the given code.
     */
    DistCodelistModel getReturnRequestReasonForCode(final String returnRequestReason);

    /**
     * Returns a list of packaging.
     * 
     * @return a list of packaging.
     */
    List<? extends DistCodelistModel> getAllReturnRequestPackaging();

    /**
     * Returns the return reason for the given code.
     * 
     * @return the return reason for the given code.
     */
    DistCodelistModel getReturnRequestPackagingForCode(final String returnRequestPackaging);

}

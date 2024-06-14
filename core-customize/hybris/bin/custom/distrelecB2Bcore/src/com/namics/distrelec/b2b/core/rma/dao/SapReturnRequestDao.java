/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.rma.dao;

import java.util.Date;
import java.util.List;

import com.namics.distrelec.b2b.core.rma.ReturnRequestData;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;


/**
 * {@code SapReturnRequestDao}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public interface SapReturnRequestDao {

    /**
     * Create a RMA object model for the SAP case from the object data and store it into the database.
     * 
     * @param customer
     *            the customer
     * @param requestData
     *            the RMA data
     * @return the RMA code generated for the newly created RMA object model
     */
    String createReturnRequest(CustomerModel customer, ReturnRequestData requestData);

    /**
     * Create a RMA object models for the SAP case from the list of object data and store them into the database.
     * 
     * @param customer
     *            the customer
     * @param requestDatas
     *            the list of RMA data
     * @return the list of RMA codes generated for the newly created RMA object models
     */
    List<String> createReturnRequests(CustomerModel customer, List<ReturnRequestData> requestDatas);

    /**
     * Look for the list of return requests of the specified user
     * 
     * @param customer
     *            the customer
     * @return the list of return requests of the current user
     */
    List<Object> getReturnRequests(CustomerModel customer);

    /**
     * Look for the list of return requests of the specified customer and order
     * 
     * @param customer
     *            the customer
     * @param order
     *            the order
     * @return the list of return requests belonging to the specified order
     */
    List<Object> getReturnRequestsByOrder(CustomerModel customer, OrderModel order);

    /**
     * Retrieve the RMA object having the specified code and belonging to the specified user
     * 
     * @param customer
     *            the customer
     * @param rmaCode
     *            the RMA code
     * @return the RMA object having the specified code
     */
    Object getReturnRequest(CustomerModel customer, String rmaCode);

    /**
     * Retrieve the list of all RMA objects from the data store
     * 
     * @return the list of all RMA objects
     */
    List<Object> findAll();

    /**
     * Retrieve all RMA objects that are created after the specified date
     * 
     * @param fromDate
     *            the start date
     * @return the list of RMA objects created after {@code fromDate}
     */
    List<Object> getReturnRequestsFromDate(Date fromDate);
    
}

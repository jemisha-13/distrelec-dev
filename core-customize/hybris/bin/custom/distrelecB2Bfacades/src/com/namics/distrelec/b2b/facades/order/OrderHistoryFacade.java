/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order;

import java.util.List;

import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 * OrderHistoryFacade.
 *
 * @author nbenothman, Distrelec
 * @since Distrelec 1.0
 */
public interface OrderHistoryFacade {

    /**
     * Returns the detail of an Order.
     *
     * @param code
     *            The code of the Order for which to retrieve the detail.
     * @return The detail of the order with matching code
     */
    OrderData getOrderForCode(final String code);

    /**
     * Retrieve a list of {@link OrderData} belonging to the current user.
     *
     * @deprecated because it does not use pagination.
     *
     * @return a list of {@code OrderData}
     */
    @Deprecated
    List<OrderData> getOrderHistory();

    /**
     * Retrieve a list of {@link OrderData} belonging to the user with the specified
     * UID.
     *
     * @deprecated because it does not use pagination.
     *
     * @return a list of {@code OrderData}
     */
    @Deprecated
    List<OrderData> getOrderHistory(final String uid);

    /**
     * Retrieve a list of {@link OrderData} belonging to the current user.
     *
     * @deprecated because it does not use pagination.
     *
     * @param status
     *            the order status list
     * @return a list of {@code OrderData}
     */
    @Deprecated
    List<OrderData> getOrderHistory(final String[] status);

    /**
     * Returns the order history of the current user for given statuses.
     *
     * @param pageableData
     *            paging information
     * @param statuses
     *            array of order statuses to filter the results
     * @return The order history of the current user.
     */
    SearchPageData<OrderHistoryData> getOrderHistory(final DistOrderHistoryPageableData pageableData,
                                                     final String[] statuses);

    /**
     * Return the order history of the current user for given order id.
     *
     * @param orderId
     * @return
     */
    OrderHistoryData getOrderHistoryById(String orderId);

    /**
     * Returns the List of all open order for the current Logged in user.
     *
     * @return The List of open order the current user.
     */
    SearchPageData<OrderHistoryData> getOpenOrders(final DistOrderHistoryPageableData pageableData);

    /**
     * Returns the detail of an Open Order.
     *
     * @param code
     *            The code of the Order for which to retrieve the detail.
     * @return The detail of the order with matching code
     */
    OrderData getOpenOrderDetailsForCode(final String code);

    /**
     * Returns the List of all open order for the current Logged in user. Returns
     * the order history of a user for given statuses.
     *
     * @param b2bCustomerModelUid
     *            the customer for which we want the orders
     * @param pageableData
     *            paging information
     * @param statuses
     *            array of order statuses to filter the results
     * @return The order history of the current user.
     */
    SearchPageData<OrderHistoryData> getOrderHistory(final String b2bCustomerModelUid,
                                                     final DistOrderHistoryPageableData pageableData, final String[] statuses);

    /**
     * checks if any order exists on ERP for contact with given uid
     *
     * @param uid
     *            the customer for which we want the orders
     * @return true if no order exists on ERP for contact with given uid
     */
    boolean isOrderHistoryEmpty(String uid);

    OrderData getOrderForObsolescence(AbstractOrderModel abstractOrderModel);

    List<OrderHistoryData> getOrdersForStatuses(final DistOrderHistoryPageableData pageableData);

    boolean updateProjectNumber(String orderCode, String workflowCode, String orderReference);
}

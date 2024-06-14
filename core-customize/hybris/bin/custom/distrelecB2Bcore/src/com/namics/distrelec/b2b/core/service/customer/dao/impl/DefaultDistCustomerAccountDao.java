/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.b2b.core.model.process.SetInitialPasswordProcessModel;
import com.namics.distrelec.b2b.core.service.customer.dao.DistCustomerAccountDao;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.customer.dao.impl.DefaultCustomerAccountDao;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Default implementation of {@link DistCustomerAccountDao}.
 * 
 * @author Nabil Benothman, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class DefaultDistCustomerAccountDao extends DefaultCustomerAccountDao implements DistCustomerAccountDao {

    private static final String FIND_ORDERS_BY_CUSTOMER_STORE_QUERY = "SELECT {" + OrderModel.PK + "}, {" + OrderModel.CREATIONTIME + "}, {" + OrderModel.CODE
                                                                      + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.USER + "} = ?"
                                                                      + OrderModel.USER + " AND {" + OrderModel.VERSIONID
                                                                      + "} IS NULL AND {" + OrderModel.STORE + "} = ?" + OrderModel.STORE;

    private static final String FIND_ORDERS_BY_CUSTOMER_STORE_QUERY_AND_STATUS = FIND_ORDERS_BY_CUSTOMER_STORE_QUERY + " AND {" + OrderModel.STATUS
                                                                                 + "} IN (?statusList)";

    private static final String FILTER_BY_ORDER_NUMBER_PREFIX = " AND {" + OrderModel.CODE + "} LIKE '%";

    private static final String FILTER_BY_ORDER_NUMBER_SUFFIX = "%' ";

    private static final String FILTER_BY_FROM_DATE = " AND {" + OrderModel.CREATIONTIME + "} >= (?fromDate) ";

    private static final String FILTER_BY_TO_DATE = " AND {" + OrderModel.CREATIONTIME + "} <= (?toDate) ";

    private static final String ORDER_BY_CODE = ", {" + OrderModel.CODE + "}";

    private static final String SORT_ORDERS_BY_DATE = " ORDER BY {" + OrderModel.CREATIONTIME + "} ";

    private static final String SORT_ORDERS_BY_STATUS = " ORDER BY {" + OrderModel.STATUS + "} ";

    private static final String SORT_ORDERS_BY_TOTAL_PRICE = " ORDER BY {" + OrderModel.TOTALPRICE + "} ";

    private static final String FIND_CUSTOMERS_BY_EMAIL = "SELECT {" + B2BCustomerModel.PK + "} FROM {" + B2BCustomerModel._TYPECODE + "} WHERE LOWER({"
                                                          + B2BCustomerModel.EMAIL + "})=?" + B2BCustomerModel.EMAIL + " AND {" + B2BCustomerModel.CUSTOMERTYPE
                                                          + "} !=?" + B2BCustomerModel.CUSTOMERTYPE;

    private static final String FIND_FORGOTTEN_PASSWORD_PROCESS_BY_TOKEN = "SELECT {" + ForgottenPasswordProcessModel.PK + "} FROM {"
                                                                           + ForgottenPasswordProcessModel._TYPECODE + "} WHERE {"
                                                                           + ForgottenPasswordProcessModel.TOKEN + "}=?" + ForgottenPasswordProcessModel.TOKEN;

    private static final String FIND_FORGOTTEN_PASSWORD_PROCESS_BY_CUSTOMER = "SELECT {" + ForgottenPasswordProcessModel.PK + "} FROM {"
                                                                              + ForgottenPasswordProcessModel._TYPECODE + "} WHERE {"
                                                                              + ForgottenPasswordProcessModel.CUSTOMER + "}=?"
                                                                              + ForgottenPasswordProcessModel.CUSTOMER;

    private static final String FIND_INITIAL_PASSWORD_PROCESS_BY_TOKEN = "SELECT {" + SetInitialPasswordProcessModel.PK + "} FROM {"
                                                                         + SetInitialPasswordProcessModel._TYPECODE + "} WHERE {"
                                                                         + SetInitialPasswordProcessModel.TOKEN + "}=?" + SetInitialPasswordProcessModel.TOKEN;

    private static final String FIND_INITIAL_PASSWORD_PROCESS_BY_CUSTOMER = "SELECT {" + SetInitialPasswordProcessModel.PK + "} FROM {"
                                                                            + SetInitialPasswordProcessModel._TYPECODE + "} WHERE {"
                                                                            + SetInitialPasswordProcessModel.CUSTOMER + "}=?"
                                                                            + SetInitialPasswordProcessModel.CUSTOMER;

    @Override
    public List<B2BCustomerModel> findCustomersByEmail(final String email) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_CUSTOMERS_BY_EMAIL);
        searchQuery.addQueryParameter(B2BCustomerModel.EMAIL, email.toLowerCase());
        searchQuery.addQueryParameter(B2BCustomerModel.CUSTOMERTYPE, CustomerType.GUEST);
        return getFlexibleSearchService().<B2BCustomerModel> search(searchQuery).getResult();
    }

    @Override
    public B2BCustomerModel findCustomerForErpContactId(final B2BUnitModel unit, final String erpContactId) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(
                                                                  "SELECT {" + B2BCustomerModel.PK + "} FROM {" + B2BCustomerModel._TYPECODE + "} WHERE {"
                                                                  + B2BCustomerModel.DEFAULTB2BUNIT + "}=?"
                                                                  + B2BCustomerModel.DEFAULTB2BUNIT + " AND{" + B2BCustomerModel.ERPCONTACTID + "}=?"
                                                                  + B2BCustomerModel.ERPCONTACTID);
        query.addQueryParameter(B2BCustomerModel.DEFAULTB2BUNIT, unit);
        query.addQueryParameter(B2BCustomerModel.ERPCONTACTID, erpContactId);
        return searchUnique(query);
    }

    @Override
    public List<B2BCustomerModel> findCustomerForErpContactId(final String erpContactId) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {" + B2BCustomerModel.PK + "} FROM {" + B2BCustomerModel._TYPECODE + "} WHERE {"
                                                                  + B2BCustomerModel.ERPCONTACTID + "}=?" + B2BCustomerModel.ERPCONTACTID);
        query.addQueryParameter(B2BCustomerModel.ERPCONTACTID, erpContactId);
        return this.<B2BCustomerModel> search(query).getResult();
    }

    @Override
    public SearchPageData<OrderModel> findOrdersByCustomerAndStore(final CustomerModel customerModel, final BaseStoreModel store, final OrderStatus[] status,
                                                                   final PageableData pageableData) {
        validateParameterNotNull(customerModel, "Customer must not be null");
        validateParameterNotNull(store, "Store must not be null");

        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(OrderModel.USER, customerModel);
        queryParams.put(OrderModel.STORE, store);

        if (status != null && status.length > 0) {
            queryParams.put("statusList", Arrays.asList(status));
        }

        final StringBuilder query = new StringBuilder(FIND_ORDERS_BY_CUSTOMER_STORE_QUERY_AND_STATUS);

        String sortType = "DESC";

        if (pageableData instanceof DistOrderHistoryPageableData) {
            final DistOrderHistoryPageableData distPageableData = (DistOrderHistoryPageableData) pageableData;
            final String orderNumber = distPageableData.getOrderNumber() != null ? distPageableData.getOrderNumber().trim() : null;
            if (StringUtils.isNotBlank(orderNumber)) {
                query.append(FILTER_BY_ORDER_NUMBER_PREFIX + orderNumber + FILTER_BY_ORDER_NUMBER_SUFFIX);
            }
            if (distPageableData.getFromDate() != null) {
                query.append(FILTER_BY_FROM_DATE);
                queryParams.put("fromDate", distPageableData.getFromDate());
            }
            if (distPageableData.getToDate() != null) {
                query.append(FILTER_BY_TO_DATE);
                queryParams.put("toDate", distPageableData.getToDate());
            }
            if (distPageableData.getSortType() != null) {
                sortType = distPageableData.getSortType();
            }
        }

        String sortCode = "byDate";

        if ("byStatus".equalsIgnoreCase(pageableData.getSort())) {
            query.append(SORT_ORDERS_BY_STATUS);
            sortCode = "byStatus";
        } else if ("byTotalPrice".equalsIgnoreCase(pageableData.getSort())) {
            query.append(SORT_ORDERS_BY_TOTAL_PRICE);
            sortCode = "byTotalPrice";
        } else {
            query.append(SORT_ORDERS_BY_DATE);
        }

        query.append(sortType).append(ORDER_BY_CODE);

        return getPagedFlexibleSearchService().search(Arrays.asList(createSortQueryData(sortCode, query.toString())), sortCode, queryParams, pageableData);
    }

    @Override
    public ForgottenPasswordProcessModel findForgottenPasswordProcessByToken(String token) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_FORGOTTEN_PASSWORD_PROCESS_BY_TOKEN);
        query.addQueryParameter(ForgottenPasswordProcessModel.TOKEN, token);
        final SearchResult<ForgottenPasswordProcessModel> result = search(query);
        return result.getCount() > 0 ? result.getResult().get(0) : null;
    }

    @Override
    public List<ForgottenPasswordProcessModel> findForgottenPasswordProcessByCustomer(CustomerModel customer) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_FORGOTTEN_PASSWORD_PROCESS_BY_CUSTOMER);
        query.addQueryParameter(ForgottenPasswordProcessModel.CUSTOMER, customer);
        return this.<ForgottenPasswordProcessModel> search(query).getResult();
    }

    @Override
    public SetInitialPasswordProcessModel findSetInitialPasswordProcessByToken(String token) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_INITIAL_PASSWORD_PROCESS_BY_TOKEN);
        query.addQueryParameter(SetInitialPasswordProcessModel.TOKEN, token);
        final SearchResult<SetInitialPasswordProcessModel> result = search(query);
        return result.getCount() > 0 ? result.getResult().get(0) : null;
    }

    @Override
    public List<SetInitialPasswordProcessModel> findSetInitialPasswordProcessByCustomer(CustomerModel customer) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_INITIAL_PASSWORD_PROCESS_BY_CUSTOMER);
        query.addQueryParameter(SetInitialPasswordProcessModel.CUSTOMER, customer);
        return this.<SetInitialPasswordProcessModel> search(query).getResult();
    }
}

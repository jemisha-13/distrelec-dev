package com.namics.distrelec.b2b.core.missingorders.dao.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.missingorders.dao.DistMissingOrdersDao;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

public class DefaultDistMissingOrdersDao implements DistMissingOrdersDao {

    private static final String FIND_SAP_MISSING_ORDERS = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.ERPORDERCODE
                                                          + "} IS NULL AND {" + OrderModel.CREATIONTIME + "} >= ?startDate AND {" + OrderModel.CREATIONTIME
                                                          + "} <= ?endDate AND {" + OrderModel.STATUS + "} NOT IN (?statusList)";

    private static final String FIND_SAP_MISSING_ORDERS_BY_DAYS = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE + "} WHERE {"
                                                                  + OrderModel.ERPORDERCODE + "} IS NULL AND {" + OrderModel.CREATIONTIME
                                                                  + "} >= ?startDate AND {" + OrderModel.STATUS + "} NOT IN (?statusList)";

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Override
    public List<OrderModel> findMissingOrders(Date fromDate, Date toDate) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_SAP_MISSING_ORDERS);
        searchQuery.addQueryParameter("startDate", fromDate);
        searchQuery.addQueryParameter("endDate", toDate);
        searchQuery.addQueryParameter("statusList", Arrays.asList(OrderStatus.PENDING_APPROVAL, OrderStatus.REJECTED));
        return getFlexibleSearchService().<OrderModel> search(searchQuery).getResult();
    }

    @Override
    public List<OrderModel> findMissingOrders(int numberOfDays) {
        Instant now = Instant.now();
        Date creationDate = Date.from(now.minus(numberOfDays, ChronoUnit.DAYS));
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_SAP_MISSING_ORDERS_BY_DAYS);
        searchQuery.addQueryParameter("startDate", creationDate);
        searchQuery.addQueryParameter("statusList", Arrays.asList(OrderStatus.PENDING_APPROVAL, OrderStatus.REJECTED));
        return getFlexibleSearchService().<OrderModel> search(searchQuery).getResult();
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

}

/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.distrelec.webservice.if15.v1.OrderSearchRequestV2;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.DistOrderHistoryCacheManager;
import com.namics.distrelec.b2b.core.inout.erp.impl.cache.SapReadAllOpenOrdersRequestCacheKey;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Default implementation of {@link DistOrderHistoryCacheManager}.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 * 
 */
public class DefaultDistOrderHistoryCacheManager implements DistOrderHistoryCacheManager {

    private static final Logger LOG = Logger.getLogger(DefaultDistOrderHistoryCacheManager.class);
    private static final String SORT_ORDER_DATE="ORDER_DATE";
    private static final String EMPTY_TEXT="";
    private static final int RESULT_SIZE=10;
    private static final int RESULT_OFFSET=1;

    @Autowired
    private UserService userService;
    
    @Autowired
    @Qualifier("sapCacheManagerFactory")
    private CacheManager distCacheManager;

    @Override
    public void clearHistoryCacheForOrder(final OrderModel order) {
        try {
            final UserModel currentUser = (order == null ? getUserService().getCurrentUser() : order.getUser());

            if (currentUser instanceof B2BCustomerModel && !getUserService().isAnonymousUser(currentUser)) {
                final B2BCustomerModel customer = ((B2BCustomerModel) currentUser);
                final B2BUnitModel unit = customer.getDefaultB2BUnit();
                removeCacheEntry(customer, unit, unit.getSalesOrg().getCode());
            }
        } catch (final Exception ex) {
            LOG.warn("Can not remove cache entry!", ex);
        }
    }

    private void removeCacheEntry(final B2BCustomerModel customer, final B2BUnitModel unit, final String salesOrg) {
        final Cache cache = distCacheManager.getCache(DistConstants.CacheName.ORDER_SEARCH);
        if (cache == null) {
            return;
        }

        // Clear open order history cache
        cache.remove(new SapReadAllOpenOrdersRequestCacheKey(salesOrg, unit.getErpCustomerID()));

        // Clear order history cache
        final OrderSearchRequestV2 orderSearchRequest = new OrderSearchRequestV2();
        orderSearchRequest.setCustomerId(unit.getErpCustomerID());
        orderSearchRequest.setSalesOrganization(salesOrg);
        orderSearchRequest.setSortCriteria(SORT_ORDER_DATE);
        orderSearchRequest.setResultSize(BigInteger.valueOf(RESULT_SIZE));
        orderSearchRequest.setResultOffset(BigInteger.valueOf(RESULT_OFFSET));
        orderSearchRequest.setFilterArticleId(EMPTY_TEXT);
        orderSearchRequest.setFilterInvoiceId(EMPTY_TEXT);
        orderSearchRequest.setFilterOrderId(EMPTY_TEXT);
        orderSearchRequest.setFilterOrderReference(EMPTY_TEXT);
        orderSearchRequest.setSortAscending(false);        
        cache.remove(orderSearchRequest);

    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

}

/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp;

import java.util.List;

import com.namics.distrelec.b2b.core.service.product.model.ProductAvailabilityExtModel;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

/**
 * Service to check stock availability.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public interface AvailabilityService {

    /**
     * Calculates the availability of a product or a list of products based on the given warehouses
     * 
     * @param productCodes
     *            a list of product codes
     * @param detailInfo
     *            if true also the pickup locations gets calculated
     * @return the stock information of the given products
     */
    List<ProductAvailabilityExtModel> getAvailability(final List<String> productCodes, final Boolean detailInfo);

    /**
     * Calculates the availability of a product or a list of products based on the given warehouses
     *
     * @param productCodes
     *            a list of product codes
     * @param detailInfo
     *            if true also the pickup locations gets calculated
     * @param useCache
     *            if true use data from cache if available
     * @return the stock information of the given products
     */
    List<ProductAvailabilityExtModel> getAvailability(final List<String> productCodes, final Boolean detailInfo, boolean useCache);

    /**
     * Calculates the availability of products of the entries on the given warehouses
     * 
     * @param entries
     *            the list of {@link AbstractOrderEntryModel}s
     * @param detailInfo
     *            if true also the pickup locations gets calculated
     * @return the stock information of the given products
     */
    List<ProductAvailabilityExtModel> getAvailabilityForEntries(final List<AbstractOrderEntryModel> entries, final Boolean detailInfo);

    /**
     * Calculates the availability of products of the entries on the given warehouses
     *
     * @param entries
     *            the list of {@link AbstractOrderEntryModel}s
     * @param detailInfo
     *            if true also the pickup locations gets calculated
     * @param useCache
     *            if true use data from cache if available
     * @return the stock information of the given products
     */
    List<ProductAvailabilityExtModel> getAvailabilityForEntries(final List<AbstractOrderEntryModel> entries, final Boolean detailInfo, boolean useCache);

    List<String> getPurchaseBlockedProductCodes();
}

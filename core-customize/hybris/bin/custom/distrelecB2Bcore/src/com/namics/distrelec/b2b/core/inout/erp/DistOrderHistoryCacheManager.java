/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp;

import de.hybris.platform.core.model.order.OrderModel;

/**
 * Manages the cache of the order calculation interface.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 * 
 */
public interface DistOrderHistoryCacheManager {

    void clearHistoryCacheForOrder(final OrderModel order);
}

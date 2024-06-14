/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp;

import de.hybris.platform.core.model.order.OrderModel;

public interface CheckoutService {

    boolean exportOrder(OrderModel order);

}

/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.strategies;

import de.hybris.platform.core.model.user.CustomerModel;

public interface CheckoutCustomerStrategy extends de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy {
    String ANONYMOUS_CHECKOUT = "anonymous_checkout";

    boolean isNotLimitedUserType(CustomerModel customer);

    boolean isEShopGroup();

    boolean isEShopGroup(CustomerModel customer);
}

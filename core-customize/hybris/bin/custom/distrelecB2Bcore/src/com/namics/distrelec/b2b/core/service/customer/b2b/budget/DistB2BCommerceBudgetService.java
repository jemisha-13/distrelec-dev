/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.b2b.budget;

import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.OrderModel;

public interface DistB2BCommerceBudgetService {

    B2BCustomerModel getUserForBudget(DistB2BBudgetModel budget);

    B2BCustomerModel getUserForBudgetCode(String code);

    DistB2BBudgetModel getActiveBudget(B2BCustomerModel customer);

    void updateBudget(OrderModel order);
}

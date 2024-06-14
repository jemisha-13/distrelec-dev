/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.b2b.budget.dao;

import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;

import de.hybris.platform.b2b.dao.B2BBudgetDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;

public interface DistB2BBudgetDao extends B2BBudgetDao {

    B2BCustomerModel findCustomerByBudget(DistB2BBudgetModel budget);

}

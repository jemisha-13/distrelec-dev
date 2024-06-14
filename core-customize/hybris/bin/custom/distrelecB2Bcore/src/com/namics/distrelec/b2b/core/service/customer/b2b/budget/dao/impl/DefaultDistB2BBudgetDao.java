/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.b2b.budget.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;
import com.namics.distrelec.b2b.core.service.customer.b2b.budget.dao.DistB2BBudgetDao;

import de.hybris.platform.b2b.dao.impl.DefaultB2BBudgetDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DefaultDistB2BBudgetDao extends DefaultB2BBudgetDao implements DistB2BBudgetDao {
    private static final Logger LOG = Logger.getLogger(DefaultDistB2BBudgetDao.class);

    @Override
    public B2BCustomerModel findCustomerByBudget(final DistB2BBudgetModel budget) {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(B2BCustomerModel._TYPECODE).append(".").append(B2BCustomerModel.PK).append("} FROM {")
                .append(B2BCustomerModel._TYPECODE).append("} WHERE {").append(B2BCustomerModel._TYPECODE).append(".").append(B2BCustomerModel.BUDGET)
                .append("}=(?").append(B2BCustomerModel.BUDGET).append(")");
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(B2BCustomerModel.BUDGET, budget);

        final SearchResult<B2BCustomerModel> result = getFlexibleSearchService().search(query.toString(), params);
        if (result.getTotalCount() > 1) {
            LOG.error("Found multiple Customers with budget-code: " + budget.getCode());
        } else if (result.getTotalCount() > 0) {
            return result.getResult().iterator().next();
        }
        return null;
    }

}

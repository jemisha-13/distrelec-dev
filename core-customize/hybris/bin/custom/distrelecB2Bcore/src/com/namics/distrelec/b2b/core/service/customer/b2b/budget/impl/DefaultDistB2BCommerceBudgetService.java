/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.b2b.budget.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;
import com.namics.distrelec.b2b.core.model.DistBudgetModificationModel;
import com.namics.distrelec.b2b.core.service.customer.b2b.budget.DistB2BCommerceBudgetService;
import com.namics.distrelec.b2b.core.service.customer.b2b.budget.dao.DistB2BBudgetDao;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;

public class DefaultDistB2BCommerceBudgetService extends AbstractBusinessService implements DistB2BCommerceBudgetService {

    private static final Logger LOG = Logger.getLogger(DefaultDistB2BCommerceBudgetService.class);

    @Autowired
    private DistB2BBudgetDao b2bBudgetDao;

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.customer.b2b.budget.DistB2BCommerceBudgetService#getUserForBudgetCode(java.lang.String)
     */
    @Override
    public B2BCustomerModel getUserForBudgetCode(final String code) {
        final DistB2BBudgetModel budget = (DistB2BBudgetModel) b2bBudgetDao.findBudgetByCode(code);
        return budget != null ? getUserForBudget(budget) : null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.customer.b2b.budget.DistB2BCommerceBudgetService#getUserForBudget(com.namics.distrelec.b2b.core
     * .model.DistB2BBudgetModel)
     */
    @Override
    public B2BCustomerModel getUserForBudget(final DistB2BBudgetModel budget) {
        return b2bBudgetDao.findCustomerByBudget(budget);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.customer.b2b.budget.DistB2BCommerceBudgetService#getActiveBudget(de.hybris.platform.b2b.model.
     * B2BCustomerModel)
     */
    @Override
    public DistB2BBudgetModel getActiveBudget(final B2BCustomerModel customer) {
        final DistB2BBudgetModel budget = customer.getBudget();
        return checkIfBudgetIsActive(budget) ? budget : null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.customer.b2b.budget.DistB2BCommerceBudgetService#updateBudget(de.hybris.platform.core.model.
     * order.OrderModel)
     */
    @Override
    public void updateBudget(final OrderModel order) {
        final B2BCustomerModel customer = (B2BCustomerModel) order.getUser();
        final DistB2BBudgetModel budget = getActiveBudget(customer);
        if (budget == null || budget.getYearlyBudget() == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("This user either has no budget or has no yearly budget, so no budget update is necessary.");
            }
            return;
        }

        final double totalPrice = order.getTotalPrice().doubleValue();
        budget.setYearlyBudget(budget.getYearlyBudget().subtract(BigDecimal.valueOf(totalPrice)));
        getModelService().save(budget);

        final DistBudgetModificationModel modification = getModelService().create(DistBudgetModificationModel.class);
        modification.setBudget(budget);
        modification.setValue(BigDecimal.valueOf(totalPrice).negate());
        modification.setCause("Place Order - " + order.getCode());
        getModelService().save(modification);
    }

    protected boolean checkIfBudgetIsActive(final DistB2BBudgetModel budget) {
        return budget != null && Boolean.TRUE.equals(budget.getActive()) && budget.getDateRange().encloses(new Date());
    }

    public DistB2BBudgetDao getB2bBudgetDao() {
        return b2bBudgetDao;
    }

    public void setB2bBudgetDao(final DistB2BBudgetDao b2bBudgetDao) {
        this.b2bBudgetDao = b2bBudgetDao;
    }

}

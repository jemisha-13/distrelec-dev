/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.b2b.budget.strategies;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;
import com.namics.distrelec.b2b.core.service.customer.b2b.budget.DistB2BCommerceBudgetService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.strategies.impl.DefaultB2BBudgetExceededEvaluationStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 * Budget Exceeded Evaluation Strategy
 * 
 * @author dsivakumaran, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class DefaultDistB2BBudgetExceededEvaluationStrategy extends DefaultB2BBudgetExceededEvaluationStrategy {

    @Autowired
    private DistB2BCommerceBudgetService distB2BCommerceBudgetService;

    @Override
    protected boolean checkBudgetExceeded(final AbstractOrderModel order) {
        boolean budgetExceeds = false;
        final B2BCustomerModel customer = (B2BCustomerModel) order.getUser();
        final DistB2BBudgetModel budget = getDistB2BCommerceBudgetService().getActiveBudget(customer);
        if (budget != null) {
            final BigDecimal totalPrice = BigDecimal.valueOf(order.getTotalPrice());
            final BigDecimal orderBudget = budget.getOrderBudget();
            final BigDecimal yearlyBudget = budget.getYearlyBudget();
            BigDecimal exceededBudget = BigDecimal.ZERO;
            if (orderBudget != null) {
                exceededBudget = totalPrice.subtract(orderBudget);
                budgetExceeds = exceededBudget.doubleValue() > 0;
            }
            if (yearlyBudget != null && !budgetExceeds) {
                exceededBudget = totalPrice.subtract(yearlyBudget);
                budgetExceeds = exceededBudget.doubleValue() > 0;
            }
        }

        return budgetExceeds;
    }

    public DistB2BCommerceBudgetService getDistB2BCommerceBudgetService() {
        return distB2BCommerceBudgetService;
    }

    public void setDistB2BCommerceBudgetService(final DistB2BCommerceBudgetService distB2BCommerceBudgetService) {
        this.distB2BCommerceBudgetService = distB2BCommerceBudgetService;
    }

}

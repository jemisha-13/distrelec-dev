/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.customer.b2b.budget.impl;

import static java.util.Objects.nonNull;

import java.math.BigDecimal;

import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;
import com.namics.distrelec.b2b.core.service.customer.b2b.budget.DistB2BCommerceBudgetService;
import com.namics.distrelec.b2b.core.service.order.DistCartService;
import com.namics.distrelec.b2b.facades.customer.b2b.budget.DistB2BBudgetFacade;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultDistB2BBudgetFacade implements DistB2BBudgetFacade {

    @Autowired
    private DistB2BCommerceBudgetService distB2BCommerceBudgetService;

    @Autowired
    private DistCartService cartService;

    @Autowired
    @Qualifier("b2bBudgetConverter")
    private Converter<DistB2BBudgetModel, B2BBudgetData> b2bBudgetConverter;

    @Autowired
    private ModelService modelService;

    @Override
    public B2BBudgetData getBudgetForCustomer(final B2BCustomerModel customer) {
        Assert.notNull(customer, "customer cannot be null");
        final DistB2BBudgetModel budgetModel = distB2BCommerceBudgetService.getActiveBudget(customer);
        if (budgetModel != null) {
            return b2bBudgetConverter.convert(budgetModel);
        }
        return null;
    }

    @Override
    public void calculateExceededBudget(final B2BBudgetData budget, final PriceData orderTotal) {
        calculateExceededBudget(budget, orderTotal.getValue());
    }

    @Override
    public void calculateExceededBudget(final B2BBudgetData budget, final BigDecimal orderTotal) {
        if (orderTotal == null || budget == null) {
            return;
        }

        // Exceeded order budget
        if (budget.getOrderBudget() != null) {
            final BigDecimal exceededOrderBudget = budget.getOrderBudget().subtract(orderTotal);
            budget.setExceededOrderBudget(exceededOrderBudget.compareTo(BigDecimal.ZERO) < 0 ? exceededOrderBudget.negate() : BigDecimal.ZERO);
        }

        // Exceeded yearly budget
        if (budget.getYearlyBudget() != null) {
            final BigDecimal exceededYearlyBudget = budget.getYearlyBudget().subtract(orderTotal);
            budget.setExceededYearlyBudget(exceededYearlyBudget.compareTo(BigDecimal.ZERO) < 0 ? exceededYearlyBudget.negate() : BigDecimal.ZERO);

            if (budget.getYearlyBudget().doubleValue() < 0) {
                budget.setYearlyBudgetUsedToDate(budget.getOriginalYearlyBudget().add(budget.getYearlyBudget().negate()));
            } else {
                budget.setYearlyBudgetUsedToDate(budget.getOriginalYearlyBudget().subtract(budget.getYearlyBudget()));
            }
        }
    }

    @Override
    public B2BBudgetData getExceededBudgetForCart() throws CartException {
        CartModel cartModel = cartService.getSessionCart();
        final B2BBudgetData budget = getBudgetForCustomer((B2BCustomerModel) cartModel.getUser());
        if (budget == null) {
            throw new CartException("Budget is null!", CartException.INVALID, cartModel.getCode());
        } else {
            calculateExceededBudget(budget, BigDecimal.valueOf(cartModel.getTotalPrice()));
        }
        cartModel.setExceededBudget(getExceededBudgetValue(budget).doubleValue());
        modelService.save(cartModel);
        return budget;
    }

    @Override
    public boolean doesOrderRequireApproval(B2BBudgetData budget) {
        return (budget.getExceededYearlyBudget() != null && budget.getExceededYearlyBudget().compareTo(BigDecimal.ZERO) > 0)
                || (budget.getExceededOrderBudget() != null && budget.getExceededOrderBudget().compareTo(BigDecimal.ZERO) > 0);
    }

    @Override
    public BigDecimal getExceededBudgetValue(B2BBudgetData budget) {
        return isYearlyBudgetExceeded(budget) ? budget.getExceededYearlyBudget() : budget.getExceededOrderBudget();
    }

    private boolean isYearlyBudgetExceeded(B2BBudgetData budget) {
        return nonNull(budget.getExceededYearlyBudget()) && budget.getExceededYearlyBudget().compareTo(BigDecimal.ZERO) > 0;
    }
}

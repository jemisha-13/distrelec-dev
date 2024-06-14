/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.inout.erp.OrderCalculationService;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCalculationService;

/**
 * Implementation of the {@link CalculationService} in the SAP case.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class SapCalculationService extends DefaultCalculationService {

    private OrderCalculationService orderCalculationService;

    // TODO: override further methods

    @Override
    public void calculate(final AbstractOrderModel order) throws CalculationException {
        if (order != null && CollectionUtils.isNotEmpty(order.getEntries())) {
            getOrderCalculationService().calculate(order, true);
        } else {
            // this means the cart must be cleaned
            cleanCartBeforeCalculation(order);
            super.calculate(order);
        }
    }

    @Override
    public void recalculate(final AbstractOrderModel order) throws CalculationException {
        if (order != null && CollectionUtils.isNotEmpty(order.getEntries())) {
            getOrderCalculationService().calculate(order, true);
        } else {
            // this means the cart must be cleaned
            cleanCartBeforeCalculation(order);
            super.recalculate(order);
        }
    }

    @Override
    public void calculateTotals(AbstractOrderModel order, boolean recalculate) throws CalculationException {
        if (order != null && CollectionUtils.isNotEmpty(order.getEntries())) {
            getOrderCalculationService().calculate(order, true);
        } else {
            // this means the cart must be cleaned
            cleanCartBeforeCalculation(order);
            super.recalculate(order);
        }
    }

    /*
     * Clean the cart of the distrelec specific attributes
     */
    private void cleanCartBeforeCalculation(final AbstractOrderModel order) {
        order.setNetDeliveryCost(Double.valueOf(0D));
        order.setNetPaymentCost(Double.valueOf(0D));
        order.setNetSubTotal(Double.valueOf(0D));
    }

    public OrderCalculationService getOrderCalculationService() {
        return orderCalculationService;
    }

    @Required
    public void setOrderCalculationService(final OrderCalculationService orderCalculationService) {
        this.orderCalculationService = orderCalculationService;
    }

}

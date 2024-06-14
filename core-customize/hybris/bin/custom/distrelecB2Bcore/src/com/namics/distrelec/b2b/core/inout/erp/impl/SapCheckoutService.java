/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.inout.erp.CheckoutService;
import com.namics.distrelec.b2b.core.inout.erp.OrderCalculationService;
import com.namics.distrelec.b2b.core.util.DistLogUtils;

import de.hybris.platform.core.model.order.OrderModel;

/**
 * Exports an order to SAP.
 * 
 * @author fbersani, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class SapCheckoutService implements CheckoutService {

    private final static Logger LOG = LogManager.getLogger(SapCheckoutService.class);

    @Autowired
    @Qualifier("erp.orderCalculationService")
    private OrderCalculationService orderCalculationService;

    @Override
    public boolean exportOrder(final OrderModel order) {
        boolean success = false;
        try {
            getOrderCalculationService().calculate(order, false);
            success = true;
        } catch (final Exception e) {
            DistLogUtils.logError(LOG, "{} {} Can not place order {} in SAP.", e, ErrorLogCode.PLACEORDER_ERROR, ErrorSource.SAP_FAULT, order.getCode());
        }

        return success;
    }

    public OrderCalculationService getOrderCalculationService() {
        return orderCalculationService;
    }

    public void setOrderCalculationService(final OrderCalculationService orderCalculationService) {
        this.orderCalculationService = orderCalculationService;
    }
}

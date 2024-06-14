/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.actions;

import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.fulfilmentprocess.actions.order.CheckAuthorizeOrderPaymentAction;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;

public class DistCheckAuthorizeOrderPaymentAction extends CheckAuthorizeOrderPaymentAction {

    @Override
    public Transition executeAction(final OrderProcessModel process) {
        final OrderModel order = process.getOrder();
        final AbstractDistPaymentModeModel paymentMode = (AbstractDistPaymentModeModel) order.getPaymentMode();
        if (Boolean.TRUE.equals(paymentMode.getHop())) {
            super.executeAction(process);
        }

        return Transition.OK;
    }
}

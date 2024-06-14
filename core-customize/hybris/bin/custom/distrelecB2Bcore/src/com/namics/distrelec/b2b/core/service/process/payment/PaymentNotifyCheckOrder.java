/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.process.payment;

import com.namics.distrelec.b2b.core.model.process.PaymentNotifyProcessModel;

import de.hybris.platform.task.RetryLaterException;

/**
 * {@code PaymentNotifyCheckOrder}
 * <p>
 * Action decision to check whether we have to place the order or not after a payment notify.
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.4
 */
public class PaymentNotifyCheckOrder extends AbstractPaymentNotifyDecisionAction {

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.processengine.action.AbstractSimpleDecisionAction#executeAction(de.hybris.platform.processengine.model.
     * BusinessProcessModel)
     */
    @Override
    public Transition executeAction(final PaymentNotifyProcessModel process) throws RetryLaterException, Exception {
        return checkContinue(process);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.process.payment.AbstractPaymentNotifyDecisionAction#getActionName()
     */
    @Override
    public String getActionName() {
        return "PaymentNotifyCheckOrder";
    }
}

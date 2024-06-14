/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.process.payment;

import java.util.Date;

import com.namics.distrelec.b2b.core.model.process.PaymentNotifyProcessModel;

import de.hybris.platform.task.RetryLaterException;

/**
 * {@code InitializePaymentNotifyTimestamp}
 * <p>
 * Initialize the status of the business process model for the payment notify. It is possible that during the execution of the current
 * business process, the customer received already payment success and therefore the order was already created. We have then the following
 * cases:
 * </p>
 * <ul>
 * <li>The payment success was not received at the time of the execution of the business process, we go to the next step (wait for X
 * minutes)</li>
 * <li>If the payment success was received at the time of the execution of the business process or no cart was found, we stop the business
 * process.</li>
 * </ul>
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.4
 */
public class InitializePaymentNotifyTimestamp extends AbstractPaymentNotifyDecisionAction {

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.processengine.action.AbstractSimpleDecisionAction#executeAction(de.hybris.platform.processengine.model.
     * BusinessProcessModel)
     */
    @Override
    public Transition executeAction(final PaymentNotifyProcessModel process) throws RetryLaterException, Exception {
        if (process.getNotifyTime() == null) { // Ensure that the time-stamp is set.
            process.setNotifyTime(new Date());
            getModelService().save(process);
        }
        return checkContinue(process);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.process.payment.AbstractPaymentNotifyDecisionAction#getActionName()
     */
    @Override
    public String getActionName() {
        return "InitializePaymentNotifyTimestamp";
    }
}

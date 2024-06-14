/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.process.payment;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.process.PaymentNotifyProcessModel;

import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.task.RetryLaterException;

/**
 * {@code PaymentNotifyCheckErpOrderCode}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.4
 */
public class PaymentNotifyCheckErpOrderCode extends AbstractPaymentNotifyDecisionAction {

    private static final Logger LOG = LogManager.getLogger(PaymentNotifyCheckErpOrderCode.class);

    @Autowired
    private B2BOrderService b2bOrderService;

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.process.payment.AbstractPaymentNotifyDecisionAction#getActionName()
     */
    @Override
    public String getActionName() {
        return "PaymentNotifyCheckErpOrderCode";
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.processengine.action.AbstractSimpleDecisionAction#executeAction(de.hybris.platform.processengine.model.
     * BusinessProcessModel)
     */
    @Override
    public Transition executeAction(final PaymentNotifyProcessModel process) throws RetryLaterException, Exception {
        LOG.info("Checking ERP code for order {}", process.getCartCode());
        try {
            final OrderModel order = getB2bOrderService().getOrderForCode(process.getCartCode());
            if (order != null && StringUtils.isNotBlank(order.getErpOrderCode())) {
                LOG.info("ERP code was received from SAP successfully for order {} -> {}", order.getCode(), order.getErpOrderCode());
                return Transition.OK;
            }
        } catch (final Exception exp) {
            LOG.error("Error occur while executing action" + getActionName(), exp);
        }

        return Transition.NOK;
    }

    public B2BOrderService getB2bOrderService() {
        return b2bOrderService;
    }

    public void setB2bOrderService(final B2BOrderService b2bOrderService) {
        this.b2bOrderService = b2bOrderService;
    }
}

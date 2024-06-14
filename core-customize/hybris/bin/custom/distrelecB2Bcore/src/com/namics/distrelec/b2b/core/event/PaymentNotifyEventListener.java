/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.event;

import static com.namics.distrelec.b2b.core.service.process.payment.PaymentNotifyProcessHelper.buildProcessCode;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.namics.distrelec.b2b.core.model.process.PaymentNotifyProcessModel;

import de.hybris.platform.processengine.model.BusinessProcessModel;

/**
 * {@code PaymentNotifyEventListener}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.4
 */
public class PaymentNotifyEventListener extends AbstractDistEventListener<PaymentNotifyEvent, PaymentNotifyProcessModel> {

    private static final Logger LOG = LogManager.getLogger(PaymentNotifyEventListener.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.event.AbstractDistEventListener#populate(de.hybris.platform.servicelayer.event.events.AbstractEvent,
     * de.hybris.platform.processengine.model.BusinessProcessModel)
     */
    @Override
    public void populate(final PaymentNotifyEvent event, final PaymentNotifyProcessModel target) {
        target.setCartCode(event.getCartCode());
        target.setUser(event.getUser());
        target.setNotifyTime(event.getNotifyTime());
        target.setPaymentMode(event.getPaymentMode());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.event.AbstractDistEventListener#createTarget()
     */
    @Override
    public PaymentNotifyProcessModel createTarget() {
        return (PaymentNotifyProcessModel) getBusinessProcessService().createProcess("paymentNotifyProcess_" + System.currentTimeMillis(),
                "paymentNotifyProcess");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.event.AbstractDistEventListener#createTarget(de.hybris.platform.servicelayer.event.events.
     * AbstractEvent)
     */
    @Override
    public PaymentNotifyProcessModel createTarget(final PaymentNotifyEvent event) {
        return (PaymentNotifyProcessModel) getBusinessProcessService()
                .createProcess(buildProcessCode(event.getUser().getUid(), event.getCartCode()), "paymentNotifyProcess");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.event.AbstractDistEventListener#validate(de.hybris.platform.servicelayer.event.events.AbstractEvent)
     */
    @Override
    protected boolean validate(final PaymentNotifyEvent event) {
        if (event.getUser() == null) {
            LOG.error("The target user cannot be null!");
            return false;
        }
        if (StringUtils.isBlank(event.getCartCode())) {
            LOG.error("The target user cart code cannot be null!");
            return false;
        }
        final BusinessProcessModel businessProcess = getBusinessProcessService().getProcess(buildProcessCode(event.getUser().getUid(), event.getCartCode()));

        if (businessProcess != null) {
            LOG.warn("Another process for this event is already started!");
            return false;
        }

        return true;
    }
}

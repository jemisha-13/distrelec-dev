/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.process.payment;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.process.PaymentNotifyProcessModel;
import com.namics.distrelec.b2b.core.service.cart.dao.DistCartDao;
import com.namics.distrelec.b2b.core.service.order.DistCommerceCartService;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.task.RetryLaterException;

/**
 * {@code AbstractPaymentNotifyDecisionAction}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.4
 */
public abstract class AbstractPaymentNotifyDecisionAction extends AbstractSimpleDecisionAction<PaymentNotifyProcessModel> {

    private static final Logger LOG = LogManager.getLogger(AbstractPaymentNotifyDecisionAction.class);

    protected final static String WAIT_DURATION_KEY = "process.payment.notify.wait";

    @Autowired
    private DistCommerceCartService commerceCartService;
    @Autowired
    private DistCartDao distCartDao;
    @Autowired
    private ConfigurationService configurationService;

    /**
     * @return the decision action name.
     */
    public abstract String getActionName();

    /**
     * Check if we should continue with the create order on payment notify.
     * 
     * @param process
     * @return {@link de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition}
     * @throws RetryLaterException
     * @throws Exception
     */
    protected Transition checkContinue(final PaymentNotifyProcessModel process) throws RetryLaterException, Exception {
        if (process == null) {
            LOG.error("The process cannot be null!");
            return Transition.NOK;
        }
        if (StringUtils.isBlank(process.getCartCode())) {
            LOG.error("The process cart code cannot be null!");
            return Transition.NOK;
        }
        if (process.getUser() == null) {
            LOG.error("The process user cannot be null!");
            return Transition.NOK;
        }
        try {
            final CartModel cart = getCommerceCartService().getCartForCodeAndUser(process.getCartCode(), process.getUser());
            if (cart == null) { // This means that either there was no such cart or the order is already placed.
                return Transition.NOK;
            }
        } catch (final Exception exp) {
            LOG.error("An error occured while processing the action " + getActionName(), exp);
            process.setEndMessage("An error occured while processing the decision action " + getActionName());
            getModelService().save(process);
            return Transition.NOK;
        }

        return Transition.OK;
    }

    // Getters & Setters

    public DistCommerceCartService getCommerceCartService() {
        return commerceCartService;
    }

    public void setCommerceCartService(final DistCommerceCartService commerceCartService) {
        this.commerceCartService = commerceCartService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public DistCartDao getDistCartDao() {
        return distCartDao;
    }

    public void setDistCartDao(DistCartDao distCartDao) {
        this.distCartDao = distCartDao;
    }
}

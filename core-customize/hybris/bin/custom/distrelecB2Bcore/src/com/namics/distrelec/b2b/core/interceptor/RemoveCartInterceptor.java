/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.interceptor;

import static de.hybris.platform.payment.enums.PaymentTransactionType.FAILED_RESPONSE;
import static de.hybris.platform.payment.enums.PaymentTransactionType.NOTIFY;
import static de.hybris.platform.payment.enums.PaymentTransactionType.SUCCESS_RESPONSE;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * {@code RemoveCartInterceptor}
 * <p>
 * A Remove Interceptor to check whether we need to delete a cart or not. A cart should not be deleted in case a payment transaction was not
 * completed. A payment transaction is considered as non complete in this interceptor if a NOTIFY request was received from the payment
 * provider, a SUCCESS status is not set and there was no failure.
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.12
 */
public class RemoveCartInterceptor implements RemoveInterceptor {

    private static final Logger LOG = LogManager.getLogger(RemoveCartInterceptor.class);
    // private static final String PAYMENT_CODES_KEY = "cart.remove.interceptor.payment.codes";

    @Autowired
    private ModelService modelService;
    @Autowired
    private ConfigurationService configurationService;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.interceptor.RemoveInterceptor#onRemove(java.lang.Object,
     * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
     */
    @Override
    public void onRemove(final Object model, final InterceptorContext ctx) throws InterceptorException {
        if (!(model instanceof CartModel) || skip((CartModel) model)) {
            return;
        }

        final CartModel cart = (CartModel) model;
        boolean success = false;
        boolean notify = false;
        boolean failure = false;
        for (final PaymentTransactionModel paymentTransaction : cart.getPaymentTransactions()) {
            if (isEmpty(paymentTransaction.getEntries())) {
                continue;
            }

            for (final PaymentTransactionEntryModel ccEntry : paymentTransaction.getEntries()) {
                if (SUCCESS_RESPONSE.equals(ccEntry.getType())) {
                    success = "OK".equals(ccEntry.getTransactionStatus());
                }
                if (NOTIFY.equals(ccEntry.getType())) {
                    notify = "OK".equals(ccEntry.getTransactionStatus());
                }
                if (FAILED_RESPONSE.equals(ccEntry.getType())) {
                    failure = true;
                }
            }
        }

        // customer / contact
        // date deleted
        // origin of delete request (maybe stack trace)

        if (notify && !success && !failure) {
            printStack(cart.getCode());
            final CartModel clone = getModelService().clone(cart);
            clone.setCode(cart.getCode() + "_deleted");
            clone.setGhostOrder(true);
            getModelService().save(clone);
        }
    }

    /**
     * Print out all the stack trace elements from all the application threads.
     * 
     * @param cartCode
     */
    private void printStack(final String cartCode) {
        final StringBuilder sb = new StringBuilder("CART delete with code " + cartCode + " ");
        Thread.getAllStackTraces().forEach((t, se) -> sb.append(Arrays.toString(se)).append("\n"));
        LOG.info(sb.toString());
    }

    /**
     * Check whether we need to skip this interceptor check. To skip a cart, one of the following condition must be fulfilled:
     * <ul>
     * <li>The cart payment mode is null</li>
     * <li>The payment transaction list is empty</li>
     * <li>The configured payment codes list is empty</li>
     * <li>The cart payment mode code is not in the list configured</li>
     * </ul>
     * 
     * @param cart
     *            the cart to check.
     * @return {@code true} if we should skip this cart, i.e., the payment mode code is not in the list to check, {@code false} otherwise.
     */
    protected boolean skip(final CartModel cart) {
        if (isEmpty(cart.getPaymentTransactions())) {
            return true;
        }

        return false;
    }

    // Getters & Setters

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}

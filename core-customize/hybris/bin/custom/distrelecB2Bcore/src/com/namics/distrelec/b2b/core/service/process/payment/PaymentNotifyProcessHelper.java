/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.process.payment;

import com.namics.distrelec.b2b.core.model.process.PaymentNotifyProcessModel;

import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 * {@code PaymentNotifyProcessHelper}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.4
 */
public final class PaymentNotifyProcessHelper {

    private static final String PREFIX = "paymentNotifyProcess";
    private static final String SUCCESS_EVENT_POSTFIX = "_PaymentSuccessEvent";
    private static final String ERP_CODE_EVENT_POSTFIX = "_ErpOrderCodeEvent";
    private static final String SEP = "-";

    /**
     * Build the process code based on the user UID and the order/cart code.
     * 
     * @param userUID
     *            the user UID
     * @param cartCode
     *            the cart code
     * @return the {@link PaymentNotifyProcessModel} code
     */
    public static String buildProcessCode(final String userUID, final String cartCode) {
        return builder(userUID, cartCode).toString();
    }

    /**
     * Build the process code based on the order user UID and the order/cart code.
     * 
     * @param order
     *            the order/cart
     * @return the {@link PaymentNotifyProcessModel} code
     * @see #buildProcessCode(String, String)
     */
    public static String buildProcessCode(final AbstractOrderModel order) {
        return buildProcessCode(order.getUser().getUid(), order.getCode()).toString();
    }

    /**
     * Build the process payment success event code based on the user UID and the cart code.
     * 
     * @param userUID
     *            the user UID
     * @param cartCode
     *            the cart code
     * @return the {@link PaymentNotifyProcessModel} event code
     */
    public static String buildProcessSuccessEventCode(final String userUID, final String cartCode) {
        return builder(userUID, cartCode).append(SUCCESS_EVENT_POSTFIX).toString();
    }

    /**
     * Build the process payment success event code based on the order/cart user UID and the order/cart code.
     * 
     * @param order
     *            the order/cart
     * @return @return the {@link PaymentNotifyProcessModel} event code
     * @see #buildProcessSuccessEventCode(String, String)
     */
    public static String buildProcessSuccessEventCode(final AbstractOrderModel order) {
        return buildProcessSuccessEventCode(order.getUser().getUid(), order.getCode()).toString();
    }

    /**
     * Build the process ERP code event code based on the user UID and the cart code.
     * 
     * @param userUID
     *            the user UID
     * @param cartCode
     *            the cart code
     * @return the {@link PaymentNotifyProcessModel} event code
     */
    public static String buildProcessErpCodeEventCode(final String userUID, final String cartCode) {
        return builder(userUID, cartCode).append(ERP_CODE_EVENT_POSTFIX).toString();
    }

    /**
     * Build the process ERP code event code based on the order/cart user UID and the order/cart code.
     * 
     * @param order
     *            the order/cart
     * @return @return the {@link PaymentNotifyProcessModel} event code
     * @see #buildProcessErpCodeEventCode(String, String)
     */
    public static String buildProcessErpCodeEventCode(final AbstractOrderModel order) {
        return buildProcessErpCodeEventCode(order.getUser().getUid(), order.getCode()).toString();
    }

    /**
     * Create the process code string builder
     * 
     * @param userUID
     *            the user UID
     * @param cartCode
     *            the cart/order code
     * @return {@link StringBuilder}
     */
    private static StringBuilder builder(final String userUID, final String cartCode) {
        return new StringBuilder(PREFIX).append(SEP).append(userUID).append(SEP).append(cartCode);
    }

    /**
     * Create a new instance of {@code PaymentNotifyProcessHelper}
     */
    private PaymentNotifyProcessHelper() {
        super();
    }
}

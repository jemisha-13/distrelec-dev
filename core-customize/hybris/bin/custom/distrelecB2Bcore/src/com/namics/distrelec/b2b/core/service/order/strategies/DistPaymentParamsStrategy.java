/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.strategies;

import java.util.Map;

import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;

import de.hybris.platform.core.model.order.CartModel;

/**
 * Strategy interface for handling payment parameters
 * 
 */
public interface DistPaymentParamsStrategy {

    /**
     * Returns all payment parameters by calling the methods getPaymentParams and getAdditionalPaymentParams.
     * 
     * @param merchantId
     * @param paymentModeCode
     * @param cartModel
     * @return map with all parameters
     */
    Map<String, String> getAllPaymentParams(final String merchantId, final String paymentModeCode, final CartModel cartModel,
            final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent);

    /**
     * Handles notify request from Payment Provider and set delivered request parameters to Payment Transaction Entry.
     * 
     * @param encryptedPaymentParamsMap
     */
    void handlePaymentNotifyParams(final Map<String, String> decryptedPaymentParamsMap, final Map<String, String> encryptedPaymentParamsMap);

    /**
     * Handles success and failure requests from Payment Provider and set delivered parameters to Payment Transaction Entry.
     * 
     * @param paymentParams
     */
    void handlePaymentSuccessFailureParams(final Map<String, String> paymentParams, final String encryptedString);

    /**
     * Checks if notify parameters are equal to current cart.
     * 
     * @param paymentParams
     */
    void checkPaymentNotify(final Map<String, String> paymentParams);

    /**
     * Checks if success or failure parameters are equal to current cart or order.
     * 
     * @param paymentParams
     */
    void checkPaymentSuccessFailure(final Map<String, String> paymentParams);

    /**
     * 
     * @param paymentMethodUrl
     * @return
     */
    String adjustURL(String paymentMethodUrl);

    /**
     * @return current card currency
     */
    String getCurrentCartCurrencyCode();

}
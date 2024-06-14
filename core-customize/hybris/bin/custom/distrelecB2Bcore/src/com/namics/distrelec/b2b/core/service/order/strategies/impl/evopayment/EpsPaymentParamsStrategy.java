/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.strategies.impl.evopayment;

import java.util.Map;

import com.namics.distrelec.b2b.core.service.order.data.PaymentTransactionData;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;

/**
 * eps Payment Method
 * 
 * @author wspalinger, Namics AG
 * 
 */
public class EpsPaymentParamsStrategy extends EvoDistPaymentParamsStrategy {

    @Override
    public String getPaymentParams(final String merchantId, final CartModel cartModel) {
        // define payment parameters
        final String refId = generaterefId(cartModel);
        final StringBuilder paymentParams = super.getDefaultPaymentParams(merchantId, cartModel, refId);

        // custom parameters
        paymentParams.append("&AccOwner=").append("dummy data");
        paymentParams.append("&AccNr=").append("dummy data");
        paymentParams.append("&AccIBAN=").append("dummy data");

        // paymentParams.append("&OrderDesc2=").append("");
        // paymentParams.append("&OptionDate=").append("");
        // paymentParams.append("&expirationTime=").append("");
        // TODO check if AccBank and BLZV are required and where to get this data
        // paymentParams.append("&AccBank=").append("");
        // paymentParams.append("&BLZV=").append("");

        // define payment transaction data
        addPaymentTransaction(merchantId, cartModel, refId, paymentParams.toString());

        return paymentParams.toString();
    }

    @Override
    public void handlePaymentNotifyParams(final Map<String, String> paymentParams, final Map<String, String> encryptedPaymentParamsMap) {
        final String encryptedString = "Len=" + encryptedPaymentParamsMap.get("Len") + "\nData=" + encryptedPaymentParamsMap.get("Data");
        final PaymentTransactionData paymentTransactionData = createPaymentTransactionData(paymentParams, encryptedString);

        paymentTransactionData.setType(PaymentTransactionType.NOTIFY);

        final PaymentTransactionModel paymentTransaction = getLatestPaymentTransaction(paymentTransactionData);
        createPaymentTransactionEntry(paymentTransaction, paymentTransactionData);
    }

    @Override
    public void handlePaymentSuccessFailureParams(final Map<String, String> paymentParams, final String encryptedString) {
        final PaymentTransactionData paymentTransactionData = createPaymentTransactionData(paymentParams, encryptedString);

        paymentTransactionData.setType(paymentParams.get("Status").equals("FAILED") ? PaymentTransactionType.FAILED_RESPONSE
                : PaymentTransactionType.SUCCESS_RESPONSE);

        final PaymentTransactionModel paymentTransaction = getLatestPaymentTransaction(paymentTransactionData);
        createPaymentTransactionEntry(paymentTransaction, paymentTransactionData);
    }

    @Override
    protected PaymentTransactionData createPaymentTransactionData(final Map<String, String> paymentParams, final String encryptedString) {
        final PaymentTransactionData paymentTransactionData = super.createPaymentTransactionData(paymentParams, encryptedString);
        paymentTransactionData.setAccIban(paymentParams.get("AccIBAN"));
        paymentTransactionData.setAccBank(paymentParams.get("AccBank"));
        paymentTransactionData.setAccNr(paymentParams.get("AccNr"));
        return paymentTransactionData;
    }

}

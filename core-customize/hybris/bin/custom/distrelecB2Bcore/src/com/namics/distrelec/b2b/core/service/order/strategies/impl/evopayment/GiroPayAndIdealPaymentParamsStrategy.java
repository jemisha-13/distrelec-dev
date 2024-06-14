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
 * Giropay and iDEAL Payment Method
 * 
 * @author wspalinger, Namics AG
 * 
 */
public class GiroPayAndIdealPaymentParamsStrategy extends EvoDistPaymentParamsStrategy {

    private static final String OTF_METHOD_DE = "PagoOTF";
    private static final String OTF_METHOD_NL = "iDEAL";

    @Override
    public boolean appendRefIdIntoOrderDesc() {
        return true;
    }

    @Override
    public String getPaymentParams(final String merchantId, final CartModel cartModel) {
        // define payment parameters
        final String refId = generaterefId(cartModel);
        final StringBuilder paymentParams = super.getDefaultPaymentParams(merchantId, cartModel, refId);

        // custom parameters
        if (getDistSalesOrgService().getCurrentSalesOrg().getCountry().getIsocode().equalsIgnoreCase("de")) {
            paymentParams.append("&otfMethod=").append(OTF_METHOD_DE);
            // paymentParams.append("&ChDesc=").append("");
        } else if (getDistSalesOrgService().getCurrentSalesOrg().getCountry().getIsocode().equalsIgnoreCase("nl")) {
            paymentParams.append("&otfMethod=").append(OTF_METHOD_NL);
        }

        paymentParams.append("&Response=").append(ENCRYPTED_RESPONSE);

        // paymentParams.append("&ShowAccNr=").append("");
        // paymentParams.append("&AccIBAN=").append("");
        // paymentParams.append("&AccNr=").append("");
        // paymentParams.append("&AccEdit=").append("");
        // paymentParams.append("&expirationTime=").append("");

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

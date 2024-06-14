/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.strategies.impl.evopayment;

import java.util.Map;

import com.namics.distrelec.b2b.core.service.order.data.PaymentTransactionData;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;

/**
 * ClickandBuy Payment Method
 * 
 * @author wspalinger, Namics AG
 * 
 */
public class ClickAndBuyPaymentParamsStrategy extends EvoDistPaymentParamsStrategy {

    @Override
    public String getPaymentParams(final String merchantId, final CartModel cartModel) {
        // define payment parameters

        final String refId = generaterefId(cartModel);
        final StringBuilder paymentParams = super.getDefaultPaymentParams(merchantId, cartModel, refId);

        // custom parameters
        paymentParams.append("&Language=").append(getCurrentLanguageIsoCode());
        paymentParams.append("&Nation=").append(getCurrentCountryIsoCode());
        paymentParams.append("&Response=").append(ENCRYPTED_RESPONSE);
        paymentParams.append("&E-Mail=").append(((B2BCustomerModel) cartModel.getUser()).getEmail());
        // contact address
        final B2BCustomerModel b2bCustomer = (B2BCustomerModel) cartModel.getUser();
        final AddressModel contactAddress = b2bCustomer.getContactAddress();
        paymentParams.append(addAddressInformation(contactAddress, "", "AddrStreetNr"));
        paymentParams.append(addAdditionalAddressInformation(contactAddress, "", false, false));
        // paymentParams.append("&Phone=").append(contactAddress.getPhone1());
        // delivery address
        paymentParams.append(addShippingAddressInformation(cartModel.getDeliveryAddress(), "sd"));
        // payment address
        paymentParams.append(addShippingAddressInformation(cartModel.getPaymentAddress(), "bd"));

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
        paymentTransactionData.setCrn(paymentParams.get("CRN"));
        return paymentTransactionData;
    }

}

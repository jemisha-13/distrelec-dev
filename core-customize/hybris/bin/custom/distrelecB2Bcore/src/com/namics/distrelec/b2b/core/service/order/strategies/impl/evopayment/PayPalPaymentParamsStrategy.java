/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.strategies.impl.evopayment;

import com.namics.distrelec.b2b.core.service.order.data.PaymentTransactionData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Paypal Payment Method
 *
 * @author wspalinger, Namics AG
 */
public class PayPalPaymentParamsStrategy extends EvoDistPaymentParamsStrategy {

    // Key --> CurrentLanguageIsoCode, value --> PayPalLanguageValue
    private static final Map<String, String> PAYPALLANGUAGEMAP = new HashMap<String, String>() {
        @Override
        public String get(Object key) {
            final String value = super.get(key);
            return value != null ? value : "en_US";
        }
    };

    // DISTRELEC-11513
    static {
        PAYPALLANGUAGEMAP.put("de", "de_DE");
        PAYPALLANGUAGEMAP.put("fr", "fr_FR");
        PAYPALLANGUAGEMAP.put("en", "en_US");
        PAYPALLANGUAGEMAP.put("bg", "bg_BG");
        PAYPALLANGUAGEMAP.put("bs", "bs_BS");
        PAYPALLANGUAGEMAP.put("cs", "cs_CZ");
        PAYPALLANGUAGEMAP.put("da", "da_DK");
        //        PAYPALLANGUAGEMAP.put("el",
        PAYPALLANGUAGEMAP.put("es", "es_ES");
        PAYPALLANGUAGEMAP.put("et", "et_EE");
        PAYPALLANGUAGEMAP.put("fi", "fi_FI");
        PAYPALLANGUAGEMAP.put("hu", "hu_HU");
        // PAYPALLANGUAGEMAP.put("is","is_IS");
        PAYPALLANGUAGEMAP.put("it", "it_IT");
        PAYPALLANGUAGEMAP.put("lt", "lt_LT");
        PAYPALLANGUAGEMAP.put("lv", "lv_LV");
        PAYPALLANGUAGEMAP.put("no", "no_NO");
        PAYPALLANGUAGEMAP.put("nl", "nl_NL");
        PAYPALLANGUAGEMAP.put("pl", "pl_PL");
        PAYPALLANGUAGEMAP.put("pt", "pt_PT");
        PAYPALLANGUAGEMAP.put("ro", "ro_RO");
        PAYPALLANGUAGEMAP.put("ru", "ru_RU");
        PAYPALLANGUAGEMAP.put("sk", "sk_SK");
        // PAYPALLANGUAGEMAP.put("sl","sl_"
        PAYPALLANGUAGEMAP.put("sv", "sv_SE");
        PAYPALLANGUAGEMAP.put("tr", "tr_TR");
    }

    @Override public String getPaymentParams(final String merchantId, final CartModel cartModel) {
        // define payment parameters
        final String refId = generaterefId(cartModel);
        final StringBuilder paymentParams = getDefaultPaymentParams(merchantId, cartModel, refId);

        // custom parameters
        String orderDesc = getOrderDesc(cartModel, appendRefIdIntoOrderDesc(), refId);
        if (orderDesc != null) {
            paymentParams.append("&OrderDesc=").append(orderDesc);
        }
        final String reqId = UUID.randomUUID().toString().replaceAll("-", "");
        paymentParams.append("&ReqID=").append(reqId);
        paymentParams.append("&Capture=").append(getCaptureMethod(cartModel));
        paymentParams.append("&Response=").append(ENCRYPTED_RESPONSE);
        paymentParams.append("&Language=").append(getPayPalLanguage());
        final AddressModel deliveryAddress = cartModel.getDeliveryAddress();
        paymentParams.append(addAddressInformation(deliveryAddress, "", "AddrStreet2"));
        // paymentParams.append("&Phone=").append(deliveryAddress.getPhone1());
        // paymentParams.append("&expirationTime=").append("");

        // according to  Sufi we have to change this to Txtype=Auth for PayPal manual capture: DISTRELEC-11513
        if ("Manual".equalsIgnoreCase(getCaptureMethod(cartModel))) {
            paymentParams.append("&Txtype=Auth");
        }

        // define payment transaction data
        addPaymentTransaction(merchantId, cartModel, refId, paymentParams.toString());

        return paymentParams.toString();
    }

    private String getOrderDesc(final CartModel cartModel, final boolean appendRefIdIntoOrderDesc, final String refId) {
        boolean isSandboxMode = getConfigurationService().getConfiguration().getBoolean("distrelec.payment.sandbox.mode");
        if (isSandboxMode){
            return null;
        }
        final StringBuilder orderDesc = new StringBuilder();
        if (appendRefIdIntoOrderDesc) {
            orderDesc.append(refId).append(";");
        }
        orderDesc.append(getOrderDescForPaymentRequest(cartModel));
        correctOrderDescStringLength(orderDesc);
        return orderDesc.toString();
    }

    private String getPayPalLanguage() {
        return PAYPALLANGUAGEMAP.get(getCurrentLanguageIsoCode().toLowerCase());
    }

    @Override
    protected boolean isTestOrder() {
        final String testOrder = getConfigurationService().getConfiguration().getString("payment.paypal.order.testmode");
        // when value is true that means there is no flow of real money this is only done for testing
        return Boolean.valueOf(testOrder);
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

        paymentTransactionData.setType("FAILED".equals(paymentParams.get("Status")) ? PaymentTransactionType.FAILED_RESPONSE
                : PaymentTransactionType.SUCCESS_RESPONSE);

        final PaymentTransactionModel paymentTransaction = getLatestPaymentTransaction(paymentTransactionData);
        createPaymentTransactionEntry(paymentTransaction, paymentTransactionData);
    }
}

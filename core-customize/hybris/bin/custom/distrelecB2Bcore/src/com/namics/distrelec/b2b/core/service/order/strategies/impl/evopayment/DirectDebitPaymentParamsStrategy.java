package com.namics.distrelec.b2b.core.service.order.strategies.impl.evopayment;

import java.util.Map;
import java.util.UUID;

import com.namics.distrelec.b2b.core.service.order.data.PaymentTransactionData;
import com.namics.distrelec.b2b.core.util.DistDateTimeUtils;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;

/**
 * DirectDebitPaymentParamsStrategy
 * 
 * @author datneerajs, Elfa Distrelec AB
 * @since Namics Extensions 3.0
 * 
 */
public class DirectDebitPaymentParamsStrategy extends EvoDistPaymentParamsStrategy {

    private final String RECURRINGDEBIT = "OOFF";
    private final String DELAYDAYS = "1";

    @Override
    public String getPaymentParams(final String merchantId, final CartModel cartModel) {
        // define payment parameters
        final String refId = generaterefId(cartModel);
        final StringBuilder paymentParams = super.getDefaultPaymentParams(merchantId, cartModel, refId);

        // custom parameters
        final String reqId = UUID.randomUUID().toString().replaceAll("-", "");

        final B2BCustomerModel currentUser = (B2BCustomerModel) cartModel.getUser();
        final B2BUnitModel currentUnit = currentUser.getDefaultB2BUnit();

        paymentParams.append("&ReqID=").append(reqId);
        paymentParams.append("&MandateID=").append(currentUnit.getErpCustomerID());
        paymentParams.append("&Capture=").append(getCaptureMethod(cartModel));
        paymentParams.append("&Response=").append(ENCRYPTED_RESPONSE);
        paymentParams.append("&Language=").append(getCurrentLanguageIsoCode().toUpperCase());

        paymentParams.append("&MdtSeqType=").append(RECURRINGDEBIT.toUpperCase());
        paymentParams.append("&SubSeqType=").append(RECURRINGDEBIT.toUpperCase());
        paymentParams.append("&DebitDelay=").append(DELAYDAYS);
        paymentParams.append("&CreditDelay=").append(DELAYDAYS);
        paymentParams.append("&DtOfSgntr=").append(DistDateTimeUtils.getDateForDirectDebit());

        paymentParams.append("&OrderDesc2=").append(getOrderDescForPaymentRequest(cartModel));

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

}

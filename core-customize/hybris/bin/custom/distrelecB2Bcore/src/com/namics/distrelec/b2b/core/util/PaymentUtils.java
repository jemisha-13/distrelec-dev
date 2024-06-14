/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util;

import com.namics.distrelec.b2b.core.model.payment.HopSupportingPaymentTransactionEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author datneerajs, Namics AG
 * @since Distrelec 1.1
 */
public class PaymentUtils {

    private static final String REF_NR = "RefNr=";
    public static final String TRANSACTION_STATUS_OK = "OK";
    public static final String TRANSACTION_STATUS_AUTHORIZED = "AUTHORIZED";

    public static String getEvoPayID(final AbstractOrderModel source) {
        return getPayIDFromSuccessfulPaymentTransactionModel(getSuccessfulPaymentTransaction(source));
    }

    public static String getEvoRefID(final AbstractOrderModel source) {
        return getRefIDFromSuccessfulPaymentTransactionModel(getSuccessfulPaymentTransaction(source));
    }

    public static String getBillingToken(final AbstractOrderModel source) {
        return getBillingTokenFromSuccessfulPaymentTransactionModel(getSuccessfulPaymentTransaction(source));
    }

    public static Double getAuthorizedAmount(final AbstractOrderModel source) {
        return getAuthorizedAmountFromSuccessfulPaymentTransactionModel(getSuccessfulPaymentTransaction(source));
    }

    public static boolean isCorrectPaymentTransaction(final PaymentTransactionEntryModel ccEntry) {
        return TRANSACTION_STATUS_OK.equalsIgnoreCase(ccEntry.getTransactionStatus()) || TRANSACTION_STATUS_AUTHORIZED.equalsIgnoreCase(ccEntry.getTransactionStatus());
    }

    private static Double getAuthorizedAmountFromSuccessfulPaymentTransactionModel(PaymentTransactionModel paymentTransaction) {
        if (paymentTransaction != null) {
            List<PaymentTransactionEntryModel> transactionEntries = paymentTransaction.getEntries();
            for (PaymentTransactionEntryModel transactionEntry : transactionEntries) {
                if (transactionEntry instanceof HopSupportingPaymentTransactionEntryModel) {
                    HopSupportingPaymentTransactionEntryModel ccEntry = (HopSupportingPaymentTransactionEntryModel) transactionEntry;
                    if (isSuccessOrNotify(ccEntry)) {
                        return ccEntry.getAmount().doubleValue();
                    }
                }
            }
        }
        return null;
    }

    private static String getBillingTokenFromSuccessfulPaymentTransactionModel(PaymentTransactionModel paymentTransaction) {
        if (paymentTransaction != null) {
            List<PaymentTransactionEntryModel> transactionEntries = paymentTransaction.getEntries();
            for (PaymentTransactionEntryModel transactionEntry : transactionEntries) {
                if (transactionEntry instanceof HopSupportingPaymentTransactionEntryModel) {
                    HopSupportingPaymentTransactionEntryModel ccEntry = (HopSupportingPaymentTransactionEntryModel) transactionEntry;
                    if (isSuccessOrNotify(ccEntry)) {
                        return ccEntry.getTransId();
                    }
                }
            }
        }
        return null;
    }

    private static boolean isSuccessOrNotify(HopSupportingPaymentTransactionEntryModel ccEntry) {
        return PaymentTransactionType.SUCCESS_RESPONSE.equals(ccEntry.getType()) || PaymentTransactionType.NOTIFY.equals(ccEntry.getType());
    }

    @SuppressWarnings("deprecation")
    private static PaymentTransactionModel getSuccessfulPaymentTransaction(final AbstractOrderModel source) {
        final List<PaymentTransactionModel> paymentTransactions = source.getPaymentTransactions();
        if (CollectionUtils.isNotEmpty(paymentTransactions)) {
            for (PaymentTransactionModel paymentTransaction : paymentTransactions) {
                final List<PaymentTransactionEntryModel> transactionEntries = paymentTransaction.getEntries();
                if (CollectionUtils.isNotEmpty(transactionEntries)) {
                    for (final PaymentTransactionEntryModel transactionEntry : transactionEntries) {
                        if (transactionEntry instanceof HopSupportingPaymentTransactionEntryModel) {
                            final HopSupportingPaymentTransactionEntryModel ccEntry = (HopSupportingPaymentTransactionEntryModel) transactionEntry;
                            if (isSuccessOrNotify(ccEntry) && isCorrectPaymentTransaction(ccEntry)) {
                                return paymentTransaction;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    private static String getRefIDFromSuccessfulPaymentTransactionModel(final PaymentTransactionModel paymentTransaction) {
        if (paymentTransaction != null) {
            final List<PaymentTransactionEntryModel> transactionEntries = paymentTransaction.getEntries();
            for (final PaymentTransactionEntryModel transactionEntry : transactionEntries) {
                if (transactionEntry instanceof HopSupportingPaymentTransactionEntryModel) {
                    final HopSupportingPaymentTransactionEntryModel ccEntry = (HopSupportingPaymentTransactionEntryModel) transactionEntry;
                    if (PaymentTransactionType.PAYMENT_REQUEST.toString().equals(ccEntry.getType().toString())) {
                        return getRefID(ccEntry.getEncryptedTransaction());
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    private static String getPayIDFromSuccessfulPaymentTransactionModel(final PaymentTransactionModel paymentTransaction) {
        if (paymentTransaction != null) {
            final List<PaymentTransactionEntryModel> transactionEntries = paymentTransaction.getEntries();
            for (final PaymentTransactionEntryModel transactionEntry : transactionEntries) {
                if (transactionEntry instanceof HopSupportingPaymentTransactionEntryModel) {
                    final HopSupportingPaymentTransactionEntryModel ccEntry = (HopSupportingPaymentTransactionEntryModel) transactionEntry;
                    if (isSuccessOrNotify(ccEntry)) {
                        return ccEntry.getPayId();
                    }
                }
            }
        }
        return null;
    }

    private static String getRefID(final String encryptedTransaction) {
        final StringBuilder sb = new StringBuilder(encryptedTransaction);
        if (sb.indexOf(REF_NR) != -1) {
            sb.delete(0, sb.indexOf(REF_NR) + REF_NR.length());
            sb.delete(sb.indexOf("&"), sb.length());
            return sb.toString();
        }
        return StringUtils.EMPTY;
    }
}

package com.namics.distrelec.b2b.core.service.order.impl;

import java.util.Calendar;

import org.springframework.stereotype.Component;

import com.namics.distrelec.b2b.core.service.order.DistCardPaymentService;

import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;

@Component
public class DefaultDistCardPaymentService implements DistCardPaymentService {
    @Override
    public boolean isCreditCardExpiryDateValid(CreditCardPaymentInfoModel creditCardPaymentInfo) {
        final int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        final int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        final int expiryMonth = Integer.parseInt(creditCardPaymentInfo.getValidToMonth());
        final int expiryYear = Integer.parseInt(creditCardPaymentInfo.getValidToYear());

        if (expiryYear > currentYear) {
            // Credit Card expire year is ahead of current year.
            // Credit Card Valid
            return Boolean.TRUE;
        } else if (expiryYear == currentYear) {
            if (expiryMonth > currentMonth) {

                // Expiry year and Current are same, then if expiry month is
                // less than current month.
                // Credit Card Valid
                return Boolean.TRUE;
            } else {

                // Credit Card Invalid
                return Boolean.FALSE;
            }
        } else {
            // Credit Card expire year is less than current year.
            // Credit Card Invalid
            return Boolean.FALSE;
        }
    }
}

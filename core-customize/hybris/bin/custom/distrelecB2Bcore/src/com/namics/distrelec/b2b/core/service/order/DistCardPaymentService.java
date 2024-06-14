package com.namics.distrelec.b2b.core.service.order;

import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;

public interface DistCardPaymentService {

    boolean isCreditCardExpiryDateValid(CreditCardPaymentInfoModel creditCardPaymentInfo);
}

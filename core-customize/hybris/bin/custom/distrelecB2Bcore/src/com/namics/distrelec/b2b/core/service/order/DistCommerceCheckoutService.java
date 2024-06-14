/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order;

import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;

public interface DistCommerceCheckoutService extends CommerceCheckoutService {

    boolean setPaymentMode(final CartModel cartModel, final PaymentModeModel paymentModeModel);

    void setAttribute(CartModel cart, String attribute, String value);

    void setDefaultPaymentAddress(final CartModel cartModel);

    void setDefaultDeliveryAddress(final CartModel cartModel);

    void setDefaultPaymentMode(final CartModel cartModel);

    void setDefaultDeliveryMode(final CartModel cartModel);

    void updateVatIdForGuest(String codiceFiscale);

}

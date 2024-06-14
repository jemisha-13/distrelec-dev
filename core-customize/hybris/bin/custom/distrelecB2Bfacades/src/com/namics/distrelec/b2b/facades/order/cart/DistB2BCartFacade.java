/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.order.cart;

import java.util.List;

import com.namics.distrelec.b2b.core.service.order.exceptions.AddToCartDisabledException;
import com.namics.distrelec.b2b.facades.message.queue.data.AddToCartBulkRequestData;
import com.namics.distrelec.b2b.facades.message.queue.data.AddToCartBulkResponseData;

import de.hybris.platform.core.model.order.CartModel;

/**
 * {@code DistB2BCartFacade}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.0
 */
public interface DistB2BCartFacade extends DistCartFacade, de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade {

    CartModel getSessionCartModel();

    void resetVoucherOnCurrentCart();

    void setVoucherCodeToRedeem(final String voucherCode);

    AddToCartBulkResponseData addToCartBulk(final AddToCartBulkRequestData addToCartBulkRequestData) throws AddToCartDisabledException;

    String getVoucherReturnCodeFromCurrentCart();

    boolean isSessionCartCalculated();

    boolean updateCustomerEmailForOOS(final String customerEmail, final List<String> articleNumber);
}

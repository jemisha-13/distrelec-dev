/*
 * Copyright 2013-2016 Distrelec Group AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.order;

import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * {@code DistCartService}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.7
 */
public interface DistCartService extends B2BCartService {

    /**
     * If existing the current session cart is being detached from this session, if {@code force == true} then this method behaves exactly
     * like {@link #removeSessionCart()}, i.e., the cart will be also deleted from the database and won't be available anymore, otherwise,
     * it is just detached from the session.
     *
     * @param force
     * @see #removeSessionCart()
     * @see #getSessionCart()
     * @see #setSessionCart(CartModel)
     */
    void removeSessionCart(final boolean force);

    /**
     * Replace the target product in the cart by the new replacement with the new quantity
     *
     * @param target
     *            the article to replace
     * @param replacement
     *            the replacement
     * @param entryNumber
     *            the position of the product in the cart
     * @param newQty
     *            the new quantity
     */
    AbstractOrderEntryModel replace(final ProductModel target, final ProductModel replacement, final int entryNumber, final long newQty);

    /**
     * Delete the backup cart from the session and from the database.
     */
    void removeBackupCart();

    boolean isCreditBlocked(CartModel cart);

    boolean isWaldom(CartModel cart);

    boolean isRs(CartModel cart);

    boolean doesQuoteAlreadyExistInCart(String quotationId);

}

/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.cart.dao;

import java.util.List;
import java.util.Optional;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

/**
 * {@code DistCartDao}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.5
 */
public interface DistCartDao extends GenericDao<CartModel> {

    /**
     * Find the {@link CartModel} which has the specified cart code.
     * 
     * @param code
     *            the cart code
     * @return the {@link CartModel} having the specified cart code;
     */
    Optional<CartModel> getCartForCode(final String code);

    /**
     * From the specified list of codes, it returns the list of product codes which are in the specified cart.
     * 
     * @param cart
     *            the cart in which to check
     * @param productCodes
     *            the list of product codes to check
     * @return the list of product codes, among the specified product codes, which are in the specified cart.
     */
    List<String> productsInCart(final CartModel cart, final String... productCodes);

}

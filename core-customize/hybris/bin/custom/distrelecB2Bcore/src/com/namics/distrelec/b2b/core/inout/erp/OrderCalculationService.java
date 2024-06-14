/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

/**
 * Service to provide order calculation options.
 * 
 * @author pbueschi, Namics AG
 * @since Distrelec 1.0
 */
public interface OrderCalculationService {

    /**
     * This method will update the cart/order in hybris with the live information from ERP.<br>
     * When simulate is true the ERP system is only calculating the cart/order.<br>
     * When simulate is false the order is placed in ERP.<br>
     * 
     * @param cart
     * @param simulate
     * @throws CalculationException
     */
    void calculate(final AbstractOrderModel cart, final boolean simulate) throws CalculationException;

    /**
     * This method will update the cart/order in hybris with the live information from ERP.<br>
     * When simulate is true the ERP system is only calculating the cart/order.<br>
     * When simulate is false the order is placed in ERP.<br>
     * 
     * With this method new order lines can be added to an existing open order.
     * 
     * @param cart
     * @param simulate
     * @param openOrderCode
     * @throws CalculationException
     */
    void calculateOpenOrder(final AbstractOrderModel cart, final boolean simulate, final String openOrderCode) throws CalculationException;

    /**
     * This method will update the wish list in hybris with the live information from ERP.<br>
     * The ERP system is only calculating the wishlist.<br>
     * 
     * @param wishlist the wish list to be calculated
     * @throws CalculationException
     */
    void calculateWishList(final Wishlist2Model wishlist) throws CalculationException;
    
}

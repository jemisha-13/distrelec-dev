/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order;

import java.util.Date;

import com.namics.distrelec.b2b.core.service.order.model.ErpOpenOrderExtModel;

import de.hybris.platform.core.model.order.AbstractOrderModel;

public interface DistOpenOrderService {

    /**
     * Create the relation between the cart to an existing open order in ERP
     * 
     * @param cart
     * @param eroOpenOrderCode
     */
    public void linkOpenOrderToCart(final AbstractOrderModel cart, final String erpCode);

    /**
     * Create the relation between the cart to a new open order, not yet created in ERP
     * 
     * @param cart
     * @param eroOpenOrderCode
     */
    public void linkOpenOrderToCart(final AbstractOrderModel cart);

    /**
     * Remove the relation between the cart and an existing or not yet existing open order in ERP.
     * 
     * @param cart
     */
    public void releaseOpenOrderFromCart(final AbstractOrderModel cart);

    /**
     * Set the closing date of the open order. (New open order checkout)
     * 
     * @param cart
     * @param date
     */
    public void setOpenOrderClosingDate(final AbstractOrderModel cart, final Date date);

    /**
     * Set the closing date of the open order. (Existing open order checkout)
     * 
     * @param cart
     *            : the specific cart to modify
     * @param code
     *            : the open order code to set to the cart
     * @param date
     *            : the closing date to set to the cart
     */
    public void setOpenOrderClosingDate(AbstractOrderModel cart, String code, Date date);

    /**
     * Specify if the open order will be editable by all accounts.
     * 
     * @param cart
     * @param isEditableByAllAccounts
     */
    public void setEditableByAllContacts(final AbstractOrderModel cart, final boolean isEditableByAllAccounts);

    /**
     * Call the calculation method for openorder to modify the open order in ERP.
     * 
     * @param openOrder
     */
    public void modifyDetailsInErp(final ErpOpenOrderExtModel openOrder);

}

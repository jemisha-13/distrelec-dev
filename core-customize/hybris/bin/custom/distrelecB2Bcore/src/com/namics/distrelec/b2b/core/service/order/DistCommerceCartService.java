/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bacceleratorservices.order.B2BCommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.exceptions.CalculationException;

public interface DistCommerceCartService extends B2BCommerceCartService {

    CommerceCartModification addToCart(CartModel cartModel, ProductModel productModel, long quantity, UnitModel unit, boolean forceNewEntry, String searchQuery)
            throws CommerceCartModificationException;

    CommerceCartModification addToCart(CartModel cartModel, ProductModel productModel, long quantity, UnitModel unit, boolean forceNewEntry, String searchQuery,
            final boolean recalculate) throws CommerceCartModificationException;

    CommerceCartModification updateCartEntry(CartModel cart, long entryNumber, String customerReference) throws CommerceCartModificationException;

    void persistCart(B2BCustomerModel customer);

    void mergeCarts(CartModel sourceCart, CartModel targetCart);

    CommerceCartModification addToCart(CartModel cartModel, ProductModel productModel, long quantityToAdd, UnitModel unit, boolean forceNewEntry,
            boolean forceEdit, final boolean calculateCart, final String ref, final boolean refCheckRequired) throws CommerceCartModificationException;

    void recalculateCart(CartModel cartModel, boolean forceEdit) throws CalculationException;

    CommerceCartModification updateCartEntry(CartModel cart, long entryNumber, String customerReference, boolean forceEdit)
            throws CommerceCartModificationException;

    CommerceCartModification updateQuantityForCartEntry(CartModel cartModel, long entryNumber, long newQuantity, boolean forceEdit)
            throws CommerceCartModificationException;

    /**
     * Updates the currency on all carts which are assigned to this user (max 2 because of persistent cart function). The carts from the
     * anonymous user will not be updated.
     * 
     * @param customer
     */
    void updateCartCurrencies(CustomerModel customer);

    long checkCartLevel(final ProductModel productModel, final CartModel cartModel);

    AbstractOrderEntryModel getOrderEntry(long entryNumber, CartModel cartModel);

    boolean isAddToCartDisabled();
}

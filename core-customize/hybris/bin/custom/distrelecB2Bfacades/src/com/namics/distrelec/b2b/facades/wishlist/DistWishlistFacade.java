/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.wishlist;

import com.namics.distrelec.b2b.core.enums.ProductListOrder;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData;

import java.util.List;

public interface DistWishlistFacade extends NamicsPrivateWishlistFacade {

    /**
     * Gets the Wishlist by the specified order.
     *
     * @param order
     *            order in which to retrieve the data
     * @return NamicsWishlistData
     */
    public NamicsWishlistData getWishlist(ProductListOrder order);

    /**
     * Gets the Wishlist with the specified uuid in the specified order.
     *
     * @param uuid
     *            uniqueId to identify the wishlist
     * @param order
     *            order in which to retrieve the data
     * @return NamicsWishlistData
     */
    public NamicsWishlistData getWishlist(String uuid, ProductListOrder order);

    /**
     * Returns true if product is in one the wishlists of this type, and false if not.
     *
     * @param code
     *            the code of the product
     * @return true or false
     */
    public boolean isProductInWishlist(String code);

    /**
     * Return the list of products that are at least in one of the wish list
     *
     * @param codes
     *            the product codes
     * @return a {@link List} of product codes
     */
    public List<String> productsInWishlist(final String[] codes);

    /**
     * Check the number of the entries in the default wish list.
     *
     * @return the number of entries in the default wish list.
     */
    public int getDefaultListSize();
}

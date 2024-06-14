/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.wishlist;

import com.namics.distrelec.b2b.facades.wishlist.data.MiniWishlistData;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData;
import de.hybris.platform.order.exceptions.CalculationException;

import java.util.List;

public interface DistShoppingListFacade extends DistWishlistFacade {

    /**
     * Create an empty wishlist with the specified name
     *
     * @param name
     *            the name of the wihslist
     * @return the wishlist converterted into the DATA-Object
     */
    public NamicsWishlistData createEmptyList(String name);

    /**
     * Deletes the specified Wishlist
     *
     * @param uuid
     *            uniqueId to identify the wishlist
     */
    public void deleteList(String uuid);

    /**
     * Updates the wishlist entry with the specified count.
     *
     * @param uuid
     *            uniqueId to identify the wishlist
     * @param productCode
     * @param desired
     *            count to be updated
     */
    public void updateProductCount(String uuid, String productCode, Integer desired);

    /**
     * Gets all public wishlists of the session user and wishlist type as minified object
     *
     * @return all public wishlists of the session user and wishlist type.
     */
    List<MiniWishlistData> getMiniWishlists();

    int getListsCount();

    /**
     * Calculates the shopping list
     *
     * @param uuid
     *            the shopping list UID
     * @throws CalculationException
     * @return the calculated shopping list
     */
    public NamicsWishlistData calculateShoppingList(final String uuid) throws CalculationException;

}

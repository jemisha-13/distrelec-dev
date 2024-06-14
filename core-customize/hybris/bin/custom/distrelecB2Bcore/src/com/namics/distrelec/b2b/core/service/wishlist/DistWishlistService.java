/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.wishlist;

import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.enums.NamicsWishlistType;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

/**
 * Distrelec wishlist service.
 * 
 * @author dsivakumaran, Namics AG
 * 
 */
public interface DistWishlistService extends NamicsWishlistService {

    void deleteWishlist(Wishlist2Model wishlist);

    Wishlist2Model createEmptyWishlist(String name, NamicsWishlistType type, Boolean isDefault);

    Wishlist2Model createEmptyWishlist(final String name, final NamicsWishlistType type, final Boolean isDefault, final UserModel user);

    void setWishlistToDefault(Wishlist2Model wishlist);

    void updateWishlistEntry(Wishlist2EntryModel entry, Integer desired);

    Wishlist2Model calculateShoppingList(final String uuid) throws CalculationException;

    int countLists(final NamicsWishlistType type);

    /**
     * Check if the given products are in any of the current user wish list
     * 
     * @param productCodes
     *            the products, given by their codes, to check.
     * @return a {@link Map} with keys as the product codes and the values as {@link List}s of {@link NamicsWishlistType}
     * @see #productsInWishlists(List, NamicsWishlistType[])
     */
    Map<String, List<String>> productsInWishlists(final List<String> productCodes);

    Map<String, List<String>> productsInShoppingList(final List<String> productCodes);

    /**
     * Check if the given products are in any of the current user wish lists given by their types. If the {@code types} is empty or
     * {@code null}, then all types will be checked.
     * 
     * @param productCodes
     *            the products, given by their codes, to check.
     * @param types
     *            the wish list types
     * @return a {@link Map} with keys as the product codes and the values as {@link List}s of {@link NamicsWishlistType}
     */
    Map<String, List<String>> productsInWishlists(final List<String> productCodes, final NamicsWishlistType... types);
}

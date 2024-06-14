/*
 * Copyright 2000-2012 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.wishlist;

import com.namics.distrelec.b2b.core.enums.NamicsWishlistType;
import com.namics.distrelec.b2b.facades.wishlist.data.MiniWishlistData;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData;

import java.util.List;

/**
 * NamicsGenericWishlistFacade.
 *
 * @author mwegener, Namics AG
 * @since Namics Extensions 1.0
 *
 */
public interface NamicsGenericWishlistFacade {

    /**
     * Gets the wishlist of the session user and wishlist type
     *
     * @param type
     *            Type of the wishlist
     * @return the Wishlist2Data
     */
    NamicsWishlistData getWishlist(final NamicsWishlistType type);

    /**
     * Gets the wishlist of the session user and wishlist type as minified object.
     *
     * @param type
     *            Type of the wishlist
     * @return the Wishlist2Data only with the totalUnitCount information
     */
    MiniWishlistData getMiniWishlist(final NamicsWishlistType type);

    /**
     * Gets all public wishlists of the session user and wishlist type as minified object.
     *
     * @param type
     *            Type of the wishlists
     * @return all public wishlists of the session user and wishlist type.
     */
    List<MiniWishlistData> getMiniWishlists(final NamicsWishlistType type);

    /**
     * Gets the public wishlist of a certain uuid.
     *
     * @param uuid
     *            unique id of the wishlist
     * @return the Wishlist2Data
     */
    NamicsWishlistData getWishlist(final String uuid);

    /**
     * Gets all public wishlists of the session user and wishlist type.
     *
     * @param type
     *            Type of the wishlist
     * @return all public wishlists of the session user and wishlist type.
     */
    List<NamicsWishlistData> getWishlists(final NamicsWishlistType type);

    /**
     * Gets the wishlist of the session user and wishlist type
     *
     * @param type
     *            Type of the wishlist
     * @param maxWishlistEntries
     *            defines how many entries will be returned (always the newest entries)
     * @return the Wishlist2Data
     */
    NamicsWishlistData getWishlist(final NamicsWishlistType type, final int maxWishlistEntries);

    /**
     * Return the number of products in the default wish list for the specified type.
     *
     * @param type
     *            the list type
     * @return the number of products in the default wish list for the specified type.
     */
    public int getWishlistSize(final NamicsWishlistType type);
}

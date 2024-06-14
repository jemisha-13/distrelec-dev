/*
 * Copyright 2000-2012 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.wishlist;

import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData;

import java.util.List;

/**
 * NamicsPrivateWishlistFacade.
 *
 * @author mwegener, Namics AG
 * @since Namics Extensions 1.0
 *
 */
public interface NamicsPrivateWishlistFacade {

    /**
     * Adds a product to the private wishlist of the session user.
     *
     * @param productCode
     *            code of product to add
     * @return true if added to private wishlist
     */
    NamicsWishlistData addToWishlist(String productCode);

    /**
     * Adds a product to the private wishlist of the session user.
     *
     * @param productCode
     *            code of product to add
     * @param customerReference
     *            customer reference
     * @return true if added to private wishlist
     */
    NamicsWishlistData addToWishlistWithCustomerReference(String productCode, String customerReference);

    /**
     * Adds a product to the private wishlist of the session user.
     *
     * @param uuid
     *            unique id of the wishlist
     * @param productCode
     *            code of product to add
     * @return true if added to private wishlist
     */
    NamicsWishlistData addToWishlist(String uuid, String productCode);

    /**
     * Adds a product to the private wishlist of the session user.
     *
     * @param uuid
     *            unique id of the wishlist
     * @param productCode
     *            code of product to add
     * @param customerReference
     *            customer reference
     * @return true if added to private wishlist
     */
    NamicsWishlistData addToWishlistWithCustomerReference(String uuid, String productCode, String customerReference);

    /**
     * Adds a product to a new wishlist (not a default wishlist) of the session user.
     *
     * @param wishlistName
     *            name of the new wishlist
     * @param wishlistDescription
     *            description of the new wishlist
     * @param productCode
     *            code of product to add
     * @return uniqueId of the new wishlist
     */
    String addToNewWishlist(String wishlistName, String wishlistDescription, String productCode);

    /**
     * Adds a product to a new wishlist (not a default wishlist) of the session user.
     *
     * @param wishlistName
     *            name of the new wishlist
     * @param wishlistDescription
     *            description of the new wishlist
     * @param productCode
     *            code of product to add
     * @param customerReference
     *            customer reference
     * @return uniqueId of the new wishlist
     */
    String addToNewWishlistWithCustomerReference(String wishlistName, String wishlistDescription, String productCode, String customerReference);

    /**
     * Removes a product from the private wishlist of the session user.
     *
     * @param productCode
     *            code of product to remove
     * @return true if removed from private wishlist
     */
    boolean removeFromWishlist(String productCode);

    /**
     * Removes a product from the private wishlist of the session user.
     *
     * @param uuid
     *            unique id of the wishlist
     * @param productCode
     *            code of product to remove
     * @return true if removed from private wishlist
     */
    boolean removeFromWishlist(String uuid, String productCode);

    /**
     * Removes all products from the private wishlist of the session user.
     *
     * @param uuid
     *            unique id of the wishlist
     * @return true if removed from private wishlist
     */
    boolean removeAllFromWishlist();

    /**
     * Gets the private wishlist of the session user
     *
     * @return the Wishlist2Data
     */
    NamicsWishlistData getWishlist();

    /**
     * Gets the private wishlist of a certain uuid.
     *
     * @param uuid
     *            unique id of the wishlist
     * @return the Wishlist2Data
     */
    NamicsWishlistData getWishlist(String uuid);

    /**
     * Gets all private wishlists of the session user.
     *
     * @return all private wishlists of the session user.
     */
    List<NamicsWishlistData> getWishlists();

    /**
     * Updates name and description of a specific wishlist
     *
     * @param uuid
     *            uniqueId to identify the wishlist
     * @param wishlistName
     *            the new name for the wishlist
     * @param wishlistDescription
     *            the new Description for the wishlist
     */
    void updateWishlistNameAndDescription(String uuid, String wishlistName, String wishlistDescription);

}
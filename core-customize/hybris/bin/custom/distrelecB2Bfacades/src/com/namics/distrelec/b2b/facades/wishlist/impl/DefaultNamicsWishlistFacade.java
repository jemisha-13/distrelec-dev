/*
 * Copyright 2000-2012 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.wishlist.impl;

import com.namics.distrelec.b2b.core.enums.NamicsWishlistType;
import com.namics.distrelec.b2b.facades.wishlist.NamicsPrivateWishlistFacade;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.wishlist2.enums.Wishlist2EntryPriority;
import de.hybris.platform.wishlist2.model.Wishlist2Model;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * DefaultNamicsPrivateWishlistFacade.
 *
 * @author mwegener, Namics AG
 * @since Namics Extensions 1.0
 *
 */
public class DefaultNamicsWishlistFacade extends DefaultNamicsGenericWishlistFacade implements NamicsPrivateWishlistFacade {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(DefaultNamicsWishlistFacade.class);

    protected NamicsWishlistType type;

    @Override
    public NamicsWishlistData addToWishlist(final String productCode) {
        final Wishlist2Model wishlist = getWishlistService().getOrCreateWishlist(getType());
        addToWishlist(productCode, wishlist, StringUtils.EMPTY);
        return convertWishlist(wishlist);
    }

    @Override
    public NamicsWishlistData addToWishlistWithCustomerReference(final String productCode, final String customerReference) {
        final Wishlist2Model wishlist = getWishlistService().getOrCreateWishlist(getType());
        addToWishlist(productCode, wishlist, customerReference);
        return convertWishlist(wishlist);
    }

    @Override
    public NamicsWishlistData addToWishlist(final String uuid, final String productCode) {
        final Wishlist2Model wishlist = getWishlistService().getWishlist(uuid);
        addToWishlist(productCode, wishlist, StringUtils.EMPTY);
        return convertWishlist(wishlist);
    }

    @Override
    public NamicsWishlistData addToWishlistWithCustomerReference(final String uuid, final String productCode, final String customerReference) {
        final Wishlist2Model wishlist = getWishlistService().getWishlist(uuid);
        addToWishlist(productCode, wishlist, customerReference);
        return convertWishlist(wishlist);
    }

    @Override
    public String addToNewWishlist(final String wishlistName, final String wishlistDescription, final String productCode) {
        final ProductModel product = getProductService().getProductForCode(productCode);
        final Wishlist2Model wishlist = getWishlistService().createEmptyDefaultWishlist(wishlistName, wishlistDescription, getType());
        getWishlistService().addWishlistEntryToWishlist(wishlist, getType(), product, Integer.valueOf(1), Wishlist2EntryPriority.LOW, StringUtils.EMPTY);
        return wishlist.getUniqueId();
    }

    @Override
    public String addToNewWishlistWithCustomerReference(final String wishlistName, final String wishlistDescription, final String productCode,
            final String customerReference) {
        final ProductModel product = getProductService().getProductForCode(productCode);
        final Wishlist2Model wishlist = getWishlistService().createEmptyDefaultWishlist(wishlistName, wishlistDescription, getType());
        getWishlistService().addWishlistEntryToWishlist(wishlist, getType(), product, Integer.valueOf(1), Wishlist2EntryPriority.LOW, customerReference);
        return wishlist.getUniqueId();
    }

    @Override
    public boolean removeFromWishlist(final String productCode) {
        final Wishlist2Model wishlist = getWishlistService().getOrCreateWishlist(getType());
        return removeFromWishlist(productCode, wishlist);
    }

    @Override
    public boolean removeFromWishlist(final String uuid, final String productCode) {
        final Wishlist2Model wishlist = getWishlistService().getWishlist(uuid);
        return removeFromWishlist(productCode, wishlist);
    }

    @Override
    public boolean removeAllFromWishlist() {
        final Wishlist2Model wishlist = getWishlistService().getOrCreateWishlist(getType());
        return removeAllFromWishlist(wishlist);
    }

    @Override
    public NamicsWishlistData getWishlist() {
        return getWishlist(getType());
    }

    @Override
    public List<NamicsWishlistData> getWishlists() {
        final long startTime = System.nanoTime();

        final List<NamicsWishlistData> wishlists = getWishlists(getType());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Execution of getWishlists() took " + (int) ((System.nanoTime() - startTime) / 1e6) + " ms.");
        }
        return wishlists;
    }

    @Override
    public void updateWishlistNameAndDescription(final String uuid, final String wishlistName, final String wishlistDescription) {
        getWishlistService().updateWishlistMetadata(uuid, wishlistName, wishlistDescription);

    }

    protected boolean addToWishlist(final String productCode, final Wishlist2Model wishlist, final String customerReference) {
        final ProductModel product = getProductService().getProductForCode(productCode);
        return getWishlistService().addWishlistEntryToWishlist(wishlist, getType(), product, Integer.valueOf(1), Wishlist2EntryPriority.LOW, customerReference);
    }

    public NamicsWishlistType getType() {
        return type;
    }

    public void setType(final NamicsWishlistType type) {
        this.type = type;
    }

}
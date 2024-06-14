/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.wishlist.impl;

import com.namics.distrelec.b2b.core.enums.NamicsWishlistType;
import com.namics.distrelec.b2b.facades.wishlist.DistShoppingListFacade;
import com.namics.distrelec.b2b.facades.wishlist.data.MiniWishlistData;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.wishlist2.enums.Wishlist2EntryPriority;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

public class DefaultDistShoppingListFacade extends AbstractDistWishlistFacade implements DistShoppingListFacade {
    private static final Logger LOG = Logger.getLogger(DefaultDistShoppingListFacade.class);

    @Override
    public String addToNewWishlist(final String wishlistName, final String wishlistDescription, final String productCode) {
        final ProductModel product = getProductService().getProductForCode(productCode);
        final Wishlist2Model wishlist = createList(wishlistName);
        getWishlistService().addWishlistEntryToWishlist(wishlist, getType(), product, Integer.valueOf(1), Wishlist2EntryPriority.LOW, StringUtils.EMPTY);
        return wishlist.getUniqueId();
    }

    @Override
    public NamicsWishlistData createEmptyList(final String name) {
        return convertWishlist(createList(name));
    }

    @Override
    public void deleteList(final String uuid) {
        final List<Wishlist2Model> wishlists = getWishlistService().getOrCreateWishlists(getType());
        if (CollectionUtils.size(wishlists) > 1) {
            final Wishlist2Model wishlist = getWishlistService().getWishlist(uuid);
            if (Boolean.TRUE.equals(wishlist.getDefault())) {
                for (final Wishlist2Model list : wishlists) {
                    if (!list.equals(wishlist)) {
                        getWishlistService().setWishlistToDefault(list);
                        break;
                    }
                }
            }
            getWishlistService().deleteWishlist(wishlist);
        }
    }

    @Override
    public NamicsWishlistType getType() {
        return NamicsWishlistType.SHOPPING;
    }

    @Override
    public void updateProductCount(final String uuid, final String productCode, final Integer desired) {
        final Wishlist2Model wishlist = getWishlistService().getWishlist(uuid);
        final ProductModel product = getProductService().getProductForCode(productCode);
        final Wishlist2EntryModel entry = getWishlistService().getWishlistEntryForProduct(product, wishlist);
        getWishlistService().updateWishlistEntry(entry, desired);
    }

    @Override
    public boolean isProductInWishlist(final String code) {
        return getWishlistService().isProductInWishlist(code, getType());
    }

    @Override
    public List<MiniWishlistData> getMiniWishlists() {
        final long startTime = System.nanoTime();
        final List<MiniWishlistData> miniWishlists = super.getMiniWishlists(getType());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Execution of getMiniWishlists() took " + (int) ((System.nanoTime() - startTime) / 1e6) + " ms.");
        }
        return miniWishlists;
    }

    protected Wishlist2Model createList(final String name) {
        return getWishlistService().createEmptyWishlist(name, getType(), Boolean.FALSE);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.wishlist.DistShoppingListFacade#calculateShoppingList(java.lang.String)
     */
    @Override
    public NamicsWishlistData calculateShoppingList(final String uuid) throws CalculationException {
        return convertWishlist(getWishlistService().calculateShoppingList(uuid));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.wishlist.DistShoppingListFacade#getListsCount()
     */
    @Override
    public int getListsCount() {
        return getWishlistService().countLists(getType());
    }
}

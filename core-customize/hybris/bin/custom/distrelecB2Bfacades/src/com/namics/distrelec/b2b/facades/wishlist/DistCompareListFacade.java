/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.wishlist;

import com.namics.distrelec.b2b.core.enums.NamicsWishlistType;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

import java.util.List;

public interface DistCompareListFacade extends DistWishlistFacade {

    public List<ProductData> getCompareList(final String cookieValue);

    public List<ProductData> getCompareList(final String cookieValue, final int listSize);

    List<ProductData> getProducts(final NamicsWishlistData wishlist, final int listSize);

    public int getCompareListSize(final String cookieValue);

    public String removeFromCompareList(final String productCode, final String cookieValue);

    public String removeAllFromCompareList(final String cookieValue);

    public String addToCompareList(final String productCode, final String cookieValue);

    public String addCookieProductsToCompareList(final String cookieValue);

    public boolean isProductInWishlist(final String code, final String cookieValue);

    public List<String> productsInCompareList(final String[] codes, final String cookieValue);

    /**
     * checks if the current user has compare products. <br />
     * for anonymous user check cookie value, for logged in user check DB.
     *
     * @param cookieValue
     *            cookie value
     * @return true, if there are any compare products
     */
    public boolean hasCompareProducts(final String cookieValue);

    /**
     * Gets the wishlist of the session user and wishlist type
     *
     * @param type
     *            Type of the wishlist
     * @return the Wishlist2Model
     */
    Wishlist2Model getWishlistModel();

}

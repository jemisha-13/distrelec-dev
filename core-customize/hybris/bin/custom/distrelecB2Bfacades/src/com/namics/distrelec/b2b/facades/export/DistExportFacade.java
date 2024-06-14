/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.export;

import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistEntryData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;

import java.io.File;
import java.util.List;

/**
 * Export facade interface.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public interface DistExportFacade {
    File exportProducts(final List<NamicsWishlistEntryData> wishlistEntries, final String exportFormat, final String exportFileNamePrefix);

    File exportCart(final CartData cartData, final String exportFormat, final String exportFileNamePrefix);

    File exportOrder(final OrderData orderData, final String exportFormat, final String exportFileNamePrefix);
}

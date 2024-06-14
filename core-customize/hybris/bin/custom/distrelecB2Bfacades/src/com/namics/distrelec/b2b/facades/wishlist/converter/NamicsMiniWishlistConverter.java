/*
 * Copyright 2000-2012 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.wishlist.converter;

import com.namics.distrelec.b2b.core.service.wishlist.TransientMiniWishlistModel;
import com.namics.distrelec.b2b.facades.wishlist.data.MiniWishlistData;
import de.hybris.platform.converters.impl.AbstractConverter;

/**
 * NamicsMiniWishlistConverter.<br />
 * This was done because the old code fetched all products just to get the total unit count.<br />
 * now the value will be retrieved directly by flexibleSearch without going over the hybris models.
 *
 * @author rlehmann, Namics AG
 * @since Distrelec 2.0.19 (DISTRELEC-4303)
 *
 */
public class NamicsMiniWishlistConverter extends AbstractConverter<TransientMiniWishlistModel, MiniWishlistData> {

    @Override
    public void populate(final TransientMiniWishlistModel source, final MiniWishlistData target) {
        if (source != null) {
            target.setUniqueId(source.getUniqueId());
            target.setName(source.getName());
            target.setTotalUnitCount(source.getTotalUnitCount());
        } else {
            target.setTotalUnitCount(0);
        }
    }
}

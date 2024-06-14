/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.cart.impl;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.core.model.order.CartModel;

/**
 * Extension of {@link DefaultCartFacade} for commercewebservices.
 */
public class DistCommerceWebServicesCartFacade extends DefaultCartFacade implements CommerceWebServicesCartFacade {
    @Override
    public CartData getSessionCart() {
        final CartModel cart = getCartService().getSessionCart();
        return getCartConverter().convert(cart);
    }

    @Override
    public boolean isAnonymousUserCart(final String cartGuid) {
        final CartModel cart = getCommerceCartService().getCartForGuidAndSiteAndUser(cartGuid, getBaseSiteService().getCurrentBaseSite(),
                                                                                     getUserService().getAnonymousUser());
        return cart != null;
    }

    @Override
    public boolean isCurrentUserCart(final String cartGuid) {
        final CartModel cart = getCommerceCartService().getCartForGuidAndSiteAndUser(cartGuid, getBaseSiteService().getCurrentBaseSite(),
                                                                                     getUserService().getCurrentUser());
        return cart != null;
    }
}

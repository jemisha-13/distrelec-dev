package com.namics.distrelec.occ.core.cart.impl;

import de.hybris.platform.commercefacades.order.CartFacade;

public interface CommerceWebServicesCartFacade extends CartFacade {
    boolean isAnonymousUserCart(final String cartGuid);

    boolean isCurrentUserCart(final String cartGuid);

}

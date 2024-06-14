/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.order.strategies;

import de.hybris.platform.commerceservices.order.CommerceAddToCartStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

/**
 * {@code DistCommerceAddToCartStrategy}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.0
 */
public interface DistCommerceAddToCartStrategy extends CommerceAddToCartStrategy {

    /**
     * 
     * @param entryToUpdate
     * @return
     * @see #isOrderEntryUpdatable(AbstractOrderEntryModel)
     */
    public boolean isOrderEntryNotUpdatable(final AbstractOrderEntryModel entryToUpdate);

    public boolean isOrderEntryUpdatable(final AbstractOrderEntryModel entryToUpdate);

    public long getAllowedCartAdjustmentForProduct(final CartModel cartModel, final ProductModel productModel, final long quantityToAdd,
            final PointOfServiceModel pointOfServiceModel);

}

/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hybris.platform.basecommerce.jalo.CartFactory;
import de.hybris.platform.basecommerce.jalo.MultiAddressInMemoryCart;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.jalo.order.delivery.DeliveryMode;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.JaloAbstractTypeException;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.jalo.user.Address;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.internal.jalo.order.InMemoryCart;

/**
 * DistInMemoryCartFactory.
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
@SuppressWarnings("deprecation")
public class DistInMemoryCartFactory implements CartFactory {

    @Override
    public Cart createCartInstance(final List<AbstractOrderEntry> entries, final Address deliveryAddress, final DeliveryMode mode, final User user,
            final Currency curr, final boolean net) throws JaloGenericCreationException, JaloAbstractTypeException {
        final ComposedType multiAddressInMemoryCartType = TypeManager.getInstance().getComposedType(MultiAddressInMemoryCart.class);

        final Map values = new HashMap();
        values.put(Cart.CODE, "TempCart");
        values.put(Cart.USER, user);
        values.put(Cart.CURRENCY, curr);
        values.put("deliveryAddress", deliveryAddress);
        values.put("deliveryMode", mode);
        values.put(Cart.NET, Boolean.valueOf(net));

        final InMemoryCart cart = (InMemoryCart) multiAddressInMemoryCartType.newInstance(values);

        for (AbstractOrderEntry entry : entries) {
            cart.addNewEntry(entry.getProduct(), entry.getQuantity().longValue(), entry.getUnit());
        }
        return cart;
    }

}

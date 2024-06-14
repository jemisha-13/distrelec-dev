/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.order.strategies.impl;

import java.util.ArrayList;
import java.util.List;

import com.namics.distrelec.b2b.core.service.order.strategies.DistDeliveryAddressesLookupStrategy;

import de.hybris.platform.commerceservices.strategies.impl.DefaultDeliveryAddressesLookupStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;

/**
 * {@code DefaultDistDeliveryAddressesLookupStrategy}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.0
 */
public class DefaultDistDeliveryAddressesLookupStrategy extends DefaultDeliveryAddressesLookupStrategy implements DistDeliveryAddressesLookupStrategy {

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.commerceservices.strategies.impl.DefaultDeliveryAddressesLookupStrategy#getDeliveryAddressesForOrder(de.hybris.
     * platform.core.model.order.AbstractOrderModel, boolean)
     */
    @Override
    public List<AddressModel> getDeliveryAddressesForOrder(final AbstractOrderModel abstractOrder, final boolean visibleAddressesOnly) {
        final List<AddressModel> addressesForOrder = new ArrayList<AddressModel>();

        if (abstractOrder != null) {
            final UserModel user = abstractOrder.getUser();
            if (user instanceof CustomerModel) {
                // We always show the delivery addresses from the company.
                addressesForOrder.addAll(getCustomerAccountService().getAddressBookDeliveryEntries((CustomerModel) user));

                // If the user had no addresses, check the order for an address in case it's a guest checkout.
                if (getCheckoutCustomerStrategy().isAnonymousCheckout() && addressesForOrder.isEmpty() && abstractOrder.getDeliveryAddress() != null) {
                    addressesForOrder.add(abstractOrder.getDeliveryAddress());
                }
            }
        }
        return addressesForOrder;
    }
}

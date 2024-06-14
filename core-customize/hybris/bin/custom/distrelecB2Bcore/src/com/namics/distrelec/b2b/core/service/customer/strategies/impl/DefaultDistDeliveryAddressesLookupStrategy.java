/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.strategies.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.strategies.impl.DefaultB2BDeliveryAddressesLookupStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;

/**
 * DefaultDistDeliveryAddressesLookupStrategy extends {@link DefaultB2BDeliveryAddressesLookupStrategy}.
 * 
 * @author dsivakumaran, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class DefaultDistDeliveryAddressesLookupStrategy extends DefaultB2BDeliveryAddressesLookupStrategy {

    @Override
    public List<AddressModel> getDeliveryAddressesForOrder(final AbstractOrderModel abstractOrder, final boolean visibleAddressesOnly) {
        final List<AddressModel> addresses = super.getDeliveryAddressesForOrder(abstractOrder, visibleAddressesOnly);
        final List<AddressModel> shippingAddresses = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(addresses)) {
            // DISTRELEC-4756: if there is just one billing address, the same address is not displayed as shipping address, otherwise the
            // logic is a handled in the frontend.
            if (visibleAddressesOnly) {
                if (abstractOrder.getUser() instanceof B2BCustomerModel) {
                    final B2BUnitModel unit = getB2bUnitService().getParent((B2BCustomerModel) abstractOrder.getUser());
                    if (unit != null && unit.getBillingAddresses().size() == 1) {
                        addresses.removeIf(o -> o.equals(abstractOrder.getPaymentAddress()));
                    }
                }
            } else {
                shippingAddresses.addAll(addresses);
            }

            return addresses;
        }
        return shippingAddresses;
    }
}

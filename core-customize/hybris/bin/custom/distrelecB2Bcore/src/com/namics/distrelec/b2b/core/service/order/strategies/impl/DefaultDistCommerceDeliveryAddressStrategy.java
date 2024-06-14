/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.order.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.List;

import de.hybris.platform.commerceservices.order.impl.DefaultCommerceDeliveryAddressStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;

/**
 * {@code DefaultDistCommerceDeliveryAddressStrategy}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.7
 */
public class DefaultDistCommerceDeliveryAddressStrategy extends DefaultCommerceDeliveryAddressStrategy {

    @Override
    public boolean storeDeliveryAddress(final CommerceCheckoutParameter parameter) {

        final CartModel cartModel = parameter.getCart();
        final AddressModel addressModel = parameter.getAddress();
        final boolean flagAsDeliveryAddress = parameter.isIsDeliveryAddress();

        validateParameterNotNull(cartModel, "Cart model cannot be null");
        getModelService().refresh(cartModel);

        final UserModel user = cartModel.getUser();
        getModelService().refresh(user);

        cartModel.setDeliveryAddress(addressModel);

        // Check that the address model belongs to the same user as the cart
        if (isValidDeliveryAddress(cartModel, addressModel)) {
            getModelService().save(cartModel);

            if (addressModel != null && flagAsDeliveryAddress && !Boolean.TRUE.equals(addressModel.getShippingAddress())) {
                // Flag the address as a delivery address
                addressModel.setShippingAddress(Boolean.TRUE);
                getModelService().save(addressModel);
            }
            if (parameter.isRecalculate()) {
                final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
                commerceCartParameter.setEnableHooks(true);
                commerceCartParameter.setCart(cartModel);
                getCommerceCartCalculationStrategy().calculateCart(commerceCartParameter);
            }
            getModelService().refresh(cartModel);
            return true;
        }
        return false;
    }

    @Override
    protected boolean isValidDeliveryAddress(final CartModel cartModel, final AddressModel addressModel) {
        if (addressModel != null) {
            final List<AddressModel> supportedAddresses = getDeliveryService().getSupportedDeliveryAddressesForOrder(cartModel,
                                                                                                                     false);
            return supportedAddresses != null && supportedAddresses.contains(addressModel);
        } else {
            return true;
        }
    }

}

/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.order.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.impl.DefaultCommerceDeliveryModeStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;

/**
 * {@code DefaultDistCommerceDeliveryModeStrategy}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.7
 */
public class DefaultDistCommerceDeliveryModeStrategy extends DefaultCommerceDeliveryModeStrategy {

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.commerceservices.order.impl.DefaultCommerceDeliveryModeStrategy#setDeliveryMode(de.hybris.platform.
     * commerceservices.service.data.CommerceCheckoutParameter)
     */
    @Override
    public boolean setDeliveryMode(final CommerceCheckoutParameter parameter) {
        final DeliveryModeModel deliveryModeModel = parameter.getDeliveryMode();
        final CartModel cartModel = parameter.getCart();

        validateParameterNotNull(cartModel, "Cart model cannot be null");
        // validateParameterNotNull(deliveryModeModel, "Delivery mode model cannot be null");

        cartModel.setDeliveryMode(deliveryModeModel);
        cartModel.setCalculated(Boolean.FALSE);

        getModelService().save(cartModel);
        if (parameter.isRecalculate()) {
            final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
            commerceCartParameter.setEnableHooks(true);
            commerceCartParameter.setCart(cartModel);
            getCommerceCartCalculationStrategy().calculateCart(commerceCartParameter);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.commerceservices.order.impl.DefaultCommerceDeliveryModeStrategy#removeDeliveryMode(de.hybris.platform.
     * commerceservices.service.data.CommerceCheckoutParameter)
     */
    @Override
    public boolean removeDeliveryMode(final CommerceCheckoutParameter parameter) {
        parameter.setDeliveryMode(null);
        return setDeliveryMode(parameter);
    }
}

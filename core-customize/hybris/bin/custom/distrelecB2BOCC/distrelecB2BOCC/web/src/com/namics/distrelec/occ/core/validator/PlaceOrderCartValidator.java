/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.validator;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import de.hybris.platform.commercefacades.order.data.CartData;

/**
 * Default commerce web services cart validator. Checks if cart is calculated and if needed values are filled.
 */
public class PlaceOrderCartValidator implements Validator {
    @Override
    public boolean supports(final Class<?> clazz) {
        return CartData.class.equals(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        final CartData cart = (CartData) target;

        if (cart.getDeliveryMode() == null) {
            errors.reject("cart.deliveryModeNotSet");
        }

        if (cart.getPaymentMode() == null) {
            errors.reject("cart.paymentModeNotSet");
        }
        if (BooleanUtils.isTrue(cart.getPaymentMode().getHop())) {
            errors.reject("cart.paymentModeIsHop");
        }
    }
}

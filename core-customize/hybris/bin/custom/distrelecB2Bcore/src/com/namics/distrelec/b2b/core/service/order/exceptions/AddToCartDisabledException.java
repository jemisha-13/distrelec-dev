package com.namics.distrelec.b2b.core.service.order.exceptions;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;

public class AddToCartDisabledException extends CommerceCartModificationException {
    public AddToCartDisabledException(String message) {
        super(message);
    }
}

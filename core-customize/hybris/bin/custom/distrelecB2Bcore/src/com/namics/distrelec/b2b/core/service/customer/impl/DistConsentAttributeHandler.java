package com.namics.distrelec.b2b.core.service.customer.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

@Deprecated
public class DistConsentAttributeHandler extends AbstractDynamicAttributeHandler<String, B2BCustomerModel> {

    @Override
    public String get(final B2BCustomerModel customer) {
        throw new UnsupportedOperationException("legacy");
    }

}

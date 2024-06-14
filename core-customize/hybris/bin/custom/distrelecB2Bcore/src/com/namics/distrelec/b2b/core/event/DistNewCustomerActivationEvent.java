package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

public class DistNewCustomerActivationEvent extends AbstractCommerceUserEvent<BaseSiteModel> {

    private String token;

    public DistNewCustomerActivationEvent(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}

/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.enums.RegistrationType;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

/**
 * DistCheckNewCustomerRegistrationEvent extends {@link AbstractCommerceUserEvent}.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class DistCheckNewCustomerRegistrationEvent extends AbstractCommerceUserEvent<BaseSiteModel> {

    private RegistrationType registrationType;

    private boolean storefrontRequest;

    public DistCheckNewCustomerRegistrationEvent(final RegistrationType registrationType, final boolean storefrontRequest) {
        super();
        this.registrationType = registrationType;
        this.storefrontRequest = storefrontRequest;
    }

    public RegistrationType getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(RegistrationType registrationType) {
        this.registrationType = registrationType;
    }

    public boolean isStorefrontRequest() {
        return storefrontRequest;
    }

    public void setStorefrontRequest(boolean storefrontRequest) {
        this.storefrontRequest = storefrontRequest;
    }
}

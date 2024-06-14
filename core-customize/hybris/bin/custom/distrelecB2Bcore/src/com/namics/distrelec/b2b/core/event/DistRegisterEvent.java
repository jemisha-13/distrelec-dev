/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.enums.RegistrationType;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

public class DistRegisterEvent extends AbstractCommerceUserEvent<BaseSiteModel> {
    private String token;
    private RegistrationType registrationType;

    /**
     * Default constructor
     */
    public DistRegisterEvent() {
        super();
    }

    /**
     * Parameterized Constructor
     * 
     * @param token
     */
    public DistRegisterEvent(final String token) {
        super();
        this.token = token;
    }

    public DistRegisterEvent(final String token, final RegistrationType registrationType) {
        super();
        this.token = token;
        this.registrationType = registrationType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public RegistrationType getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(RegistrationType registrationType) {
        this.registrationType = registrationType;
    }

}

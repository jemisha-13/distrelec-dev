/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

/**
 * Forgotten password event, implementation of {@link AbstractCommerceUserEvent}
 */
public class SetInitialPwdEvent extends AbstractCommerceUserEvent<BaseSiteModel> {

    private String token;

    private boolean storefrontRequest;

    private boolean skipEmailSending;

    /**
     * Default constructor
     */
    public SetInitialPwdEvent() {
        super();
    }

    /**
     * Parameterized Constructor
     * 
     * @param token
     * @param storefrontRequest
     */
    public SetInitialPwdEvent(final String token, final boolean storefrontRequest) {
        super();
        this.token = token;
        this.storefrontRequest = storefrontRequest;
        this.skipEmailSending = false;
    }

    public SetInitialPwdEvent(String token, boolean storefrontRequest, boolean skipEmailSending) {
        this.token = token;
        this.storefrontRequest = storefrontRequest;
        this.skipEmailSending = skipEmailSending;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public boolean isStorefrontRequest() {
        return storefrontRequest;
    }

    public void setStorefrontRequest(boolean storefrontRequest) {
        this.storefrontRequest = storefrontRequest;
    }

    public boolean isSkipEmailSending() {
        return skipEmailSending;
    }

    public void setSkipEmailSending(boolean skipEmailSending) {
        this.skipEmailSending = skipEmailSending;
    }
}

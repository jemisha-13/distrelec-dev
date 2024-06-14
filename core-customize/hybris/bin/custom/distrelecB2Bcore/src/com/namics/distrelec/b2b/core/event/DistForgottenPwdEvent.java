package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

public class DistForgottenPwdEvent extends AbstractCommerceUserEvent<BaseSiteModel> {
    private String token;
    private boolean storefrontRequest;

    public DistForgottenPwdEvent(String token, boolean storefrontRequest) {
        this.token = token;
        this.storefrontRequest = storefrontRequest;
    }

    public String getToken() {
        return token;
    }

    public boolean isStorefrontRequest() {
        return storefrontRequest;
    }
}

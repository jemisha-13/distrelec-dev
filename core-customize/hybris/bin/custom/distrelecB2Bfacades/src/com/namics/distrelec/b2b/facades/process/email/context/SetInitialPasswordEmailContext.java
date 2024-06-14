/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.model.process.SetInitialPasswordProcessModel;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SetInitialPasswordEmailContext extends CustomerEmailContext {
    private int expiresInMinutes = 30;
    private String token;
    private boolean storefrontRequest;

    @Autowired
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    public int getExpiresInMinutes() {
        return expiresInMinutes;
    }

    public void setExpiresInMinutes(final int expiresInMinutes) {
        this.expiresInMinutes = expiresInMinutes;
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

    @Override
    public String getURLEncodedToken() throws UnsupportedEncodingException {
        return URLEncoder.encode(token, "UTF-8");
    }

    public String getResetPasswordUrl() throws UnsupportedEncodingException {
        return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), false, "/account/password/setinitialpw", "token=" + getURLEncodedToken()+ "&lang=" + getCustomer().getLanguage().getIsocode());
    }

    public String getSecureResetPasswordUrl() throws UnsupportedEncodingException {
        return isStorefrontRequest() ? getSecureResetPasswordUrlForStorefront() : getSecureResetPasswordUrlForHeadless();
    }

    private String getSecureResetPasswordUrlForHeadless() throws UnsupportedEncodingException {
        return distSiteBaseUrlResolutionService.getHeadlessWebsiteUrlForSite(getBaseSite(), true, "/account/password/setinitialpw", "token=" + getURLEncodedToken()+ "&lang=" + getCustomer().getLanguage().getIsocode());
    }

    private String getSecureResetPasswordUrlForStorefront() throws UnsupportedEncodingException {
        return distSiteBaseUrlResolutionService.getStorefrontWebsiteUrlForSite(getBaseSite(), true, "/account/password/setinitialpw") + "?" + "token=" + getURLEncodedToken()+ "&lang=" + getCustomer().getLanguage().getIsocode();
    }

    public String getDisplayResetPasswordUrl() throws UnsupportedEncodingException {
        return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), false, "/my-account/set-password");
    }

    public String getDisplaySecureResetPasswordUrl() throws UnsupportedEncodingException {
        return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), true, "/my-account/set-password");
    }

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof SetInitialPasswordProcessModel) {
            setToken(((SetInitialPasswordProcessModel) businessProcessModel).getToken());
            setStorefrontRequest(((SetInitialPasswordProcessModel) businessProcessModel).getStorefrontRequest());
        }
    }
}

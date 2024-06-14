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
package com.namics.distrelec.b2b.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;

/**
 * Velocity context for a forgotten password email.
 */
public class ForgottenPasswordEmailContext extends CustomerEmailContext {
    private static final String TOKEN_PARAM = "token=";
    private static final String LANGUAGE_PARAM = "&lang=";

    private int expiresInMinutes = 120;
    private String token;
    private boolean inCheckout;
    private boolean storefrontRequest;

    @Autowired
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    public boolean isInCheckout() {
        return inCheckout;
    }

    public void setInCheckout(boolean inCheckout) {
        this.inCheckout = inCheckout;
    }

    public boolean isStorefrontRequest() {
        return storefrontRequest;
    }

    public void setStorefrontRequest(boolean storefrontRequest) {
        this.storefrontRequest = storefrontRequest;
    }

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

    @Override
    public String getURLEncodedToken() throws UnsupportedEncodingException {
        return URLEncoder.encode(token, StandardCharsets.UTF_8);
    }

    public String getResetPasswordUrl() throws UnsupportedEncodingException {
        return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), false, "/login/pw/change", TOKEN_PARAM + getURLEncodedToken());
    }

    public String getSecureResetPasswordUrl() throws UnsupportedEncodingException {
        return isStorefrontRequest() ? getSecureResetPasswordUrlForStorefront() : getSecureResetPasswordUrlForHeadless();
    }

    private String getSecureResetPasswordUrlForHeadless() throws UnsupportedEncodingException {
        if (isInCheckout()) {
            return distSiteBaseUrlResolutionService.getHeadlessWebsiteUrlForSite(getBaseSite(), true, "/login/checkout/pw/change",
                                                                          TOKEN_PARAM + getURLEncodedToken() + LANGUAGE_PARAM + getCustomer().getLanguage()
                                                                                                                                             .getIsocode());
        }
        return distSiteBaseUrlResolutionService.getHeadlessWebsiteUrlForSite(getBaseSite(), true, "/login/pw/change",
                                                                      TOKEN_PARAM + getURLEncodedToken() + LANGUAGE_PARAM + getCustomer().getLanguage().getIsocode());
    }

    private String getSecureResetPasswordUrlForStorefront() throws UnsupportedEncodingException {
        if (isInCheckout()) {
            return distSiteBaseUrlResolutionService.getStorefrontWebsiteUrlForSite(getBaseSite(), true, "/login/checkout/pw/change") + "?" +
                    TOKEN_PARAM + getURLEncodedToken() + LANGUAGE_PARAM + getCustomer().getLanguage().getIsocode();
        }
        return distSiteBaseUrlResolutionService.getStorefrontWebsiteUrlForSite(getBaseSite(), true, "/login/pw/change") + "?" +
                TOKEN_PARAM + getURLEncodedToken() + LANGUAGE_PARAM + getCustomer().getLanguage().getIsocode();
    }

    public String getDisplayResetPasswordUrl() throws UnsupportedEncodingException {
        return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), false, "/my-account/update-password");
    }

    public String getDisplaySecureResetPasswordUrl() throws UnsupportedEncodingException {
        return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), true, "/my-account/update-password");
    }

    public String getSecureRequestResetPasswordUrl() throws UnsupportedEncodingException {
        return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), true, "/login/pw/request");
    }

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof ForgottenPasswordProcessModel) {
            setToken(((ForgottenPasswordProcessModel) businessProcessModel).getToken());
            setInCheckout(((ForgottenPasswordProcessModel) businessProcessModel).isInCheckout());
            setStorefrontRequest(((ForgottenPasswordProcessModel) businessProcessModel).getStorefrontRequest());
        }
    }
}

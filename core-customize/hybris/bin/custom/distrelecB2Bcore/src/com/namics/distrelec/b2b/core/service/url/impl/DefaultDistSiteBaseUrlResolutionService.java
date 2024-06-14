/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.url.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;

import de.hybris.platform.acceleratorservices.urlresolver.impl.DefaultSiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

/**
 * DefaultDistSiteBaseUrlResolutionService extends DefaultSiteBaseUrlResolutionService.
 * 
 * @author lstuker, Namics AG
 * @since Distrelec 1.0
 */
public class DefaultDistSiteBaseUrlResolutionService extends DefaultSiteBaseUrlResolutionService implements DistSiteBaseUrlResolutionService {

    private static final String CMS_SITE_MODEL_CANNOT_BE_NULL_MSG = "CMS site model cannot be null";

    @Override
    public String getMobileUrlForSite(final BaseSiteModel site, final boolean secure, final String path) {
        validateParameterNotNull(site, "CMS site model cannot be null");

        final String url = cleanupUrl(lookupConfig("mobile." + site.getUid() + (secure ? ".https" : ".http")));

        if (url != null) {
            return url + (path == null ? "" : path);
        }
        return "";

    }

    @Override
    public String getWebsiteUrlForSiteIncludeBasicAuth(final BaseSiteModel site, final boolean secure, final String path) {
        validateParameterNotNull(site, "CMS site model cannot be null");

        final String url = cleanupUrl(lookupConfig("website." + site.getUid() + (secure ? ".https" : ".http")));

        if (url != null) {
            final StringBuffer urlIncludingBassicAuth = new StringBuffer();
            updateBasicAuthUrl(getProtocol(url), urlIncludingBassicAuth, url);
            return urlIncludingBassicAuth.toString() + (path == null ? "" : path);
        }
        return getDefaultWebsiteUrlForSite(site, secure, path);
    }

    private String getProtocol(final String url) {
        final String protocolArr[] = { "https://", "http://" };
        for (int i = 0; i < protocolArr.length; i++) {
            if (url.startsWith(protocolArr[i]))
                return protocolArr[i];
        }
        return null;
    }

    private void updateBasicAuthUrl(final String protocol, final StringBuffer urlIncludingBassicAuth, final String url) {
        urlIncludingBassicAuth.append(protocol);
        urlIncludingBassicAuth.append(getAribaBasicAuthString());
        urlIncludingBassicAuth.append(url.substring(url.indexOf(protocol) + protocol.length()));
    }

    private String getAribaBasicAuthString() {
        return getConfigurationService().getConfiguration().getString("ariba.basic.auth.string", "");
    }

    @Override
    public String getMobileUrlForSiteAndUseragent(final BaseSiteModel site, final boolean secure, final String path,
                                                  final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent) {
        validateParameterNotNull(site, "CMS site model cannot be null");

        final String url = cleanupUrl(lookupConfig(userAgent.getConfigKey() + "." + site.getUid() + (secure ? ".https" : ".http")));

        if (url != null) {
            return url + (path == null ? "" : path);
        }

        return "";
    }

    @Override
    public String getLocalMediaUrl(boolean secure) {
        return getDefaultMediaUrlForSite(null, secure);
    }

    @Override
    public String getHeadlessWebsiteUrlForSite(BaseSiteModel site, boolean secure, String path) {
        validateParameterNotNull(site, "CMS site model cannot be null");

        final String url = cleanupUrl(lookupConfig("website.headless." + site.getUid() + (secure ? ".https" : ".http")));

        if (url != null) {
            final StringBuffer urlIncludingBassicAuth = new StringBuffer();
            updateBasicAuthUrl(getProtocol(url), urlIncludingBassicAuth, url);
            return urlIncludingBassicAuth.toString() + (path == null ? "" : path);
        }
        return getDefaultWebsiteUrlForSite(site, secure, path);
    }

    @Override
    public String getHeadlessWebsiteUrlForSite(BaseSiteModel site, boolean secure, String path, String queryParams) {
        final String url = getHeadlessWebsiteUrlForSite(site, secure, path);
        return appendQueryParams(url, queryParams);
    }

    @Override
    public String getStorefrontWebsiteUrlForSite(BaseSiteModel site, boolean secure, String path) {
        return getStorefrontWebsiteUrlForSite(site, getUrlEncoderService().getUrlEncodingPattern(), secure, path);
    }

    @Override
    public String getApiWebsiteUrlForSite(BaseSiteModel site, boolean secure, String path) {
        return getApiWebsiteUrlForSite(site, getUrlEncoderService().getUrlEncodingPattern(), secure, path);
    }

    private String getApiWebsiteUrlForSite(final BaseSiteModel site, final String encodingAttributes, final boolean secure,
                                           final String path) {
        validateParameterNotNull(site, CMS_SITE_MODEL_CANNOT_BE_NULL_MSG);

        final String url = cleanupUrl(lookupConfig("ccv2.occ.backend.base.url.distrelecStore.value"));
        if (url != null) {
            // if url contains ? remove everything after ? then add path then add back the query string
            // this is so website urls in config files can have query strings and urls in emails will be
            // formatted correctly
            if (url.contains("?")) {
                final String queryString = url.substring(url.indexOf('?'));
                final String tmpUrl = url.substring(0, url.indexOf('?'));
                return cleanupUrl(tmpUrl) + (StringUtils.isNotBlank(encodingAttributes) ? encodingAttributes : StringUtils.EMPTY)
                       + (path == null ? StringUtils.EMPTY : path) + "/" + queryString;
            }

            return url + (StringUtils.isNotBlank(encodingAttributes) ? encodingAttributes : StringUtils.EMPTY) + (path == null ? StringUtils.EMPTY : path);
        }
        return getDefaultWebsiteUrlForSite(site, secure, path);
    }

    private String getStorefrontWebsiteUrlForSite(final BaseSiteModel site, final String encodingAttributes, final boolean secure,
                                                  final String path) {
        validateParameterNotNull(site, CMS_SITE_MODEL_CANNOT_BE_NULL_MSG);

        final String url = cleanupUrl(lookupConfig("website.storefront." + site.getUid() + (secure ? ".https" : ".http")));
        if (url != null) {
            // if url contains ? remove everything after ? then add path then add back the query string
            // this is so website urls in config files can have query strings and urls in emails will be
            // formatted correctly
            if (url.contains("?")) {
                final String queryString = url.substring(url.indexOf('?'));
                final String tmpUrl = url.substring(0, url.indexOf('?'));
                return cleanupUrl(tmpUrl) + (StringUtils.isNotBlank(encodingAttributes) ? encodingAttributes : "")
                       + (path == null ? "" : path) + "/" + queryString;
            }

            return url + (StringUtils.isNotBlank(encodingAttributes) ? encodingAttributes : "") + (path == null ? "" : path);
        }
        return getDefaultWebsiteUrlForSite(site, secure, path);
    }
}

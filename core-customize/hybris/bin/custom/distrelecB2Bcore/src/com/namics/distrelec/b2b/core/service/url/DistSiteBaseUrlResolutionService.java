/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.url;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

public interface DistSiteBaseUrlResolutionService extends SiteBaseUrlResolutionService {
    /**
     * Resolves mobile website base url for the given site.
     * 
     * @param site
     *            the base site
     * @param secure
     *            flag to indicate is HTTPS url is required
     * @param path
     *            the path to include in the url
     * @return The URL for the website
     */
    String getMobileUrlForSite(BaseSiteModel site, boolean secure, String path);

    /**
     * 
     * @param site
     * @param secure
     * @param path
     * @param userAgent
     * @return the user agent enum
     */
    String getMobileUrlForSiteAndUseragent(final BaseSiteModel site, final boolean secure, final String path,
            final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent);

    /**
     * use this method for getting website Url for Ariba which include basic auth token in the url
     * 
     * @param site
     * @param secure
     * @param path
     * @return
     */
    String getWebsiteUrlForSiteIncludeBasicAuth(final BaseSiteModel site, final boolean secure, final String path);

    String getLocalMediaUrl(boolean secure);

    /**
     * Returns a shop domain on which is a headless storefront.
     */
    String getHeadlessWebsiteUrlForSite(BaseSiteModel site, boolean secure, String path);

    /**
     * Returns a shop domain on which is a headless storefront.
     */
    String getHeadlessWebsiteUrlForSite(BaseSiteModel site, boolean secure, String path, final String queryParams);

    /**
     * Returns a shop domain on which is an accelerator storefront.
     */
    String getStorefrontWebsiteUrlForSite(BaseSiteModel site, boolean secure, String path);

    String getApiWebsiteUrlForSite(BaseSiteModel site, boolean secure, String path);

    /**
     * @author rhaemmerli, Namics AG
     * @since Namics Extensions 1.0
     *
     */
    public enum MobileUserAgent {
        NONE("", ""), SEVENVAL_DEFAULT("Sevenval FIT", "mobile"), SEVENVAL_PREVIEW("Sevenval FIT Preview", "mobile.preview");

        private final String userAgentString;
        private final String configKey;

        private MobileUserAgent(String userAgentString, String confikKey) {
            this.userAgentString = userAgentString;
            this.configKey = confikKey;
        }

        public String getUserAgentString() {
            return userAgentString;
        }

        public String getConfigKey() {
            return configKey;
        }

        public static MobileUserAgent getByAgentString(String agentString) {
            for (MobileUserAgent agent : MobileUserAgent.values()) {
                if (agent.getUserAgentString().equals(agentString)) {
                    return agent;
                }
            }

            return NONE;
        }
    }
}

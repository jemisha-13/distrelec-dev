/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap;

import java.util.List;

import com.namics.distrelec.b2b.core.service.sitemap.utils.WebSitemapHelper;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Sitemap Service.
 * 
 * @author csieber, Namics AG
 * @since Namics Extensions 1.0
 */
public interface WebSitemapService {

    void generateSitemapForSite(final CMSSiteModel cmsSiteModel, String domain, List<WebSitemapUrlsProvider> webSitemapUrlsProviderList);

    void setConfigurationService(final ConfigurationService configurationService);

    void setWebSitemapHelper(final WebSitemapHelper webSitemapHelper);
}

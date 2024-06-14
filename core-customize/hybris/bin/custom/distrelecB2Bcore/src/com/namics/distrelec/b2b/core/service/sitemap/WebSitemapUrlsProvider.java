/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator.WebSitemapUrl;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Get a list of websitemap url's
 * 
 * @author csieber, Namics AG
 * @since Namics Extensions 1.0
 */
public interface WebSitemapUrlsProvider {

    Collection<WebSitemapUrl> getWebSitemapUrlsForWebsite();

    Map<LanguageModel, Collection<WebSitemapUrl>> getLanguageWebSitemapUrlsForWebsite();

    String getEntityName();

    void setModelService(final ModelService modelService);

    void setBlackList(final Collection<String> blackList);

    Set<LanguageModel> getAlternativeLanguages();

    void init();
}

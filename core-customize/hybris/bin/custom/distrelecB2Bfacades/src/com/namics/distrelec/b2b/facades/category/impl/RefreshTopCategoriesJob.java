package com.namics.distrelec.b2b.facades.category.impl;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;

class RefreshTopCategoriesJob implements Runnable {

    private final TopCategoriesCacheKey cacheKey;

    private final CalculateTopCategoriesHook calculateHook;

    private final ConcurrentMap<TopCategoriesCacheKey, Object> topCategoryBackgroundRefreshLocks;

    private final CatalogVersionService catalogVersionService;

    private final CommonI18NService commonI18NService;

    private final I18NService i18NService;

    private final Collection<CatalogVersionModel> sessionCatalogVersions;

    RefreshTopCategoriesJob(TopCategoriesCacheKey cacheKey, CalculateTopCategoriesHook calculateHook,
                            ConcurrentMap<TopCategoriesCacheKey, Object> topCategoryBackgroundRefreshLocks, CatalogVersionService catalogVersionService,
                            CommonI18NService commonI18NService, I18NService i18NService) {
        this.cacheKey = cacheKey;
        this.calculateHook = calculateHook;
        this.topCategoryBackgroundRefreshLocks = topCategoryBackgroundRefreshLocks;
        this.catalogVersionService = catalogVersionService;
        this.commonI18NService = commonI18NService;
        this.i18NService = i18NService;
        this.sessionCatalogVersions = catalogVersionService.getSessionCatalogVersions();
    }

    @Override
    public void run() {
        try {
            catalogVersionService.setSessionCatalogVersions(sessionCatalogVersions);
            LanguageModel language = commonI18NService.getLanguage(cacheKey.getLanguageIsocode());
            commonI18NService.setCurrentLanguage(language);
            i18NService.setLocalizationFallbackEnabled(true);
            calculateHook.calculateTopCategoryData(cacheKey);
        } finally {
            topCategoryBackgroundRefreshLocks.remove(cacheKey);
        }
    }
}

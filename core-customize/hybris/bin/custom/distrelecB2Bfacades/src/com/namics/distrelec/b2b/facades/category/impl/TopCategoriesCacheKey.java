package com.namics.distrelec.b2b.facades.category.impl;

import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.regioncache.key.CacheUnitValueType;

import java.util.Objects;

/**
 * Cache key for getting or putting top categories from cache
 */
class TopCategoriesCacheKey implements CacheKey {

    private final String tenantId;
    private final String languageIsocode;

    public TopCategoriesCacheKey(Tenant tenant, LanguageModel language) {
        this.tenantId = tenant.getTenantID();
        this.languageIsocode = language.getIsocode();
    }

    @Override
    public CacheUnitValueType getCacheValueType() {
        return null;
    }

    @Override
    public Object getTypeCode() {
        return null;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    public String getLanguageIsocode() {
        return languageIsocode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopCategoriesCacheKey that = (TopCategoriesCacheKey) o;
        return Objects.equals(languageIsocode, that.languageIsocode) && Objects.equals(tenantId, that.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(languageIsocode, tenantId);
    }
}

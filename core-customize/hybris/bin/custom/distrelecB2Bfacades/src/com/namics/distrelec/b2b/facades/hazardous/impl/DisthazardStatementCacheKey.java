package com.namics.distrelec.b2b.facades.hazardous.impl;

import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.regioncache.key.CacheUnitValueType;

import java.util.Objects;

class DisthazardStatementCacheKey implements CacheKey {


    private final String tenantId;
    private final String langIsocode;

    public DisthazardStatementCacheKey(final Tenant tenant, final LanguageModel language) {
        this.tenantId = tenant.getTenantID();
        this.langIsocode = language.getIsocode();
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
        return langIsocode;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DisthazardStatementCacheKey that = (DisthazardStatementCacheKey) o;
        return Objects.equals(langIsocode, that.langIsocode) && Objects.equals(tenantId, that.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(langIsocode, tenantId);
    }
}

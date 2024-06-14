/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.classification.util;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.namics.distrelec.b2b.core.service.product.model.cassandra.CProductFeatures;

/**
 * {@code CProductFeaturesCacheKey}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.9
 */
public class CProductFeaturesCacheKey implements Serializable {

    private static final int PRIME = 37;

    private final String product;
    private final String language;

    /**
     * Create a new instance of {@code CProductFeaturesCacheKey}
     * 
     * @param product
     * @param language
     */
    public CProductFeaturesCacheKey(final String product, final String language) {
        this.product = product;
        this.language = language;
    }

    /**
     * Create a new instance of {@code CProductFeaturesCacheKey}
     * 
     * @param cpf
     */
    public CProductFeaturesCacheKey(final CProductFeatures cpf) {
        this(cpf.getProduct(), cpf.getLanguage());
    }

    @Override
    public int hashCode() {
        return PRIME * Objects.hashCode(product, language);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof CProductFeaturesCacheKey)) {
            return false;
        }

        final CProductFeaturesCacheKey other = (CProductFeaturesCacheKey) obj;

        return Objects.equal(this.product, other.product) && Objects.equal(this.language, other.language);
    }
}

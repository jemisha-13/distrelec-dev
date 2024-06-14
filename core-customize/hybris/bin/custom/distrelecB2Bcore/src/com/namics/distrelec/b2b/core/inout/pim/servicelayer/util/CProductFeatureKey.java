package com.namics.distrelec.b2b.core.inout.pim.servicelayer.util;

import java.util.Locale;
import java.util.Objects;

import com.namics.distrelec.b2b.core.service.product.model.cassandra.CProductFeature;

public class CProductFeatureKey {

    private final Locale locale;
    private final String featureQualifier;
    private final int featureValuePosition;

    public CProductFeatureKey(Locale locale, CProductFeature feature) {
        this(locale, feature.getQualifier(), feature.getValuePosition());
    }

    public CProductFeatureKey(Locale locale, String featureQualifier, int featureValuePosition) {
        this.locale = locale;
        this.featureQualifier = featureQualifier;
        this.featureValuePosition = featureValuePosition;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CProductFeatureKey other = (CProductFeatureKey) obj;

        if (!locale.equals(other.locale)) {
            return false;
        }
        if (!featureQualifier.equals(other.featureQualifier)) {
            return false;
        }
        if (featureValuePosition != other.featureValuePosition) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(locale, featureQualifier, featureValuePosition);
    }
}

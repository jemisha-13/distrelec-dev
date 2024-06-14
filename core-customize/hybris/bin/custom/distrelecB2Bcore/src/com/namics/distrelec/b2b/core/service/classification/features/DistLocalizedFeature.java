package com.namics.distrelec.b2b.core.service.classification.features;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.LocalizedFeature;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DistLocalizedFeature extends LocalizedFeature {

    private final int featurePosition;

    public DistLocalizedFeature(ClassAttributeAssignmentModel assignment, Map<Locale, List<FeatureValue>> values,
            Locale currentLocale, int featurePosition) {
        super(assignment, values, currentLocale);
        this.featurePosition = featurePosition;
    }

    public int getFeaturePosition() {
        return featurePosition;
    }
}

package com.namics.distrelec.b2b.core.service.classification.features;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.UnlocalizedFeature;

import java.util.List;

public class DistUnlocalizedFeature extends UnlocalizedFeature {

    private final int featurePosition;

    public DistUnlocalizedFeature(ClassAttributeAssignmentModel assignment, List<FeatureValue> values, int featurePosition) {
        super(assignment, values);
        this.featurePosition = featurePosition;
    }

    public int getFeaturePosition() {
        return featurePosition;
    }
}

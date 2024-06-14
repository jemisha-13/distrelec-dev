/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.comparator;

import java.util.Comparator;

import de.hybris.platform.catalog.model.ProductFeatureModel;

/**
 * Compare two ProductFeatureModels.
 * 
 * @author ascherrer, Namics AG
 * @since v3.3
 * 
 */
public class ProductFeatureModelComparator implements Comparator<ProductFeatureModel> {

    @Override
    public int compare(final ProductFeatureModel first, final ProductFeatureModel other) {
        Integer firstAssignmentPosition = Integer.valueOf(0);
        if (first.getClassificationAttributeAssignment() != null && first.getClassificationAttributeAssignment().getPosition() != null) {
            firstAssignmentPosition = first.getClassificationAttributeAssignment().getPosition();
        }
        Integer otherAssignmentPosition = Integer.valueOf(0);
        if (other.getClassificationAttributeAssignment() != null && other.getClassificationAttributeAssignment().getPosition() != null) {
            otherAssignmentPosition = other.getClassificationAttributeAssignment().getPosition();
        }

        if (firstAssignmentPosition.intValue() != otherAssignmentPosition.intValue()) {
            return firstAssignmentPosition.intValue() - otherAssignmentPosition.intValue();
        }

        final Integer firstValuePosition = first.getValuePosition() == null ? Integer.valueOf(0) : first.getValuePosition();
        final Integer otherValuePosition = other.getValuePosition() == null ? Integer.valueOf(0) : other.getValuePosition();

        return firstValuePosition.intValue() - otherValuePosition.intValue();
    }
}

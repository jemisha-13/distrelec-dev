/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.comparator;

import java.util.Comparator;

import com.namics.distrelec.b2b.core.service.product.model.cassandra.CProductFeature;

/**
 * {@code CProductFeatureComparator}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.6
 */
public class CProductFeatureComparator implements Comparator<CProductFeature> {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(final CProductFeature first, final CProductFeature other) {
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

        final Integer firstFeaturePosition = first.getFeaturePosition();
        final Integer otherFeaturePosition = other.getFeaturePosition();
        if (firstFeaturePosition != null && otherFeaturePosition != null) {
            int featurePosComparison = firstFeaturePosition.intValue() - otherFeaturePosition.intValue();
            if (featurePosComparison != 0) {
                return featurePosComparison;
            }
        }

        final Integer firstValuePosition = first.getValuePosition() == null ? Integer.valueOf(0) : first.getValuePosition();
        final Integer otherValuePosition = other.getValuePosition() == null ? Integer.valueOf(0) : other.getValuePosition();

        return firstValuePosition.intValue() - otherValuePosition.intValue();
    }

}

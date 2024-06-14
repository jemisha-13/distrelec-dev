package com.namics.distrelec.b2b.backoffice.classification.comparator;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.platformbackoffice.classification.comparator.ClassificationClassComparator;

public class DistClassificationClassComparator extends ClassificationClassComparator {

    @Override
    public int compare(ClassificationClassModel leftObject, ClassificationClassModel rightObject) {
        if (leftObject.getSupercategories() != null && leftObject.getSupercategories().contains(rightObject)) {
            return 1;
        } else {
            return rightObject.getSupercategories() != null && rightObject.getSupercategories().contains(leftObject) ? -1 : getNameOrCode(leftObject).compareToIgnoreCase(getNameOrCode(rightObject));
        }
    }

    protected String getNameOrCode(ClassificationClassModel classificationClassModel) {
        return classificationClassModel.getName() != null ? classificationClassModel.getName() : classificationClassModel.getCode();
    }
}

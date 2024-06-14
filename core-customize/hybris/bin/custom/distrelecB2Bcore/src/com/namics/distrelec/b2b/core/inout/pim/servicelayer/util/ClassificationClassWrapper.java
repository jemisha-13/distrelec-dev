/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;

/**
 * Wrapper for {@link ClassificationClassModel} to hold a temporary list with newly created {@link ClassAttributeAssignmentModel}s.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class ClassificationClassWrapper {

    private final ClassificationClassModel classificationClass;
    private final List<ClassAttributeAssignmentModel> classAttributeAssignments = new ArrayList<ClassAttributeAssignmentModel>();
    private final Map<String, ClassAttributeAssignmentModel> nonEmptyClassAttributeAssignments = new HashMap<String, ClassAttributeAssignmentModel>();

    /**
     * Creates a new instance of {@code ClassificationClassWrapper}
     * 
     * @param classificationClass
     */
    public ClassificationClassWrapper(final ClassificationClassModel classificationClass) {
        this.classificationClass = classificationClass;

        if (classificationClass != null && CollectionUtils.isNotEmpty(classificationClass.getDeclaredClassificationAttributeAssignments())) {
            classAttributeAssignments.addAll(classificationClass.getDeclaredClassificationAttributeAssignments());
        }
    }

    /**
     * Add a new class attribute assignment to the related classification class.
     * 
     * @param assignment
     */
    public void addClassAttributeAssignment(final ClassAttributeAssignmentModel assignment) {
        if (assignment != null) {
            classAttributeAssignments.add(assignment);
            if (assignment.getClassificationAttribute() != null && assignment.getClassificationAttribute().getCode() != null
                    && !nonEmptyClassAttributeAssignments.containsKey(assignment.getClassificationAttribute().getCode())) {
                nonEmptyClassAttributeAssignments.put(assignment.getClassificationAttribute().getCode(), assignment);
            }
        }
    }

    /**
     * Return the {@code ClassAttributeAssignmentModel} matching the specified code. In addition, if the found object is not in the non
     * empty attributes, then it is added to that list.
     * 
     * @param classificationAttributeCode
     * @return the {@code ClassAttributeAssignmentModel} matching the specified code.
     */
    public ClassAttributeAssignmentModel getClassAttributeAssignment(final String classificationAttributeCode) {
        for (final ClassAttributeAssignmentModel assignment : classAttributeAssignments) {
            if (assignment.getClassificationAttribute().getCode().equals(classificationAttributeCode)) {
                if (!nonEmptyClassAttributeAssignments.containsKey(classificationAttributeCode)) {
                    nonEmptyClassAttributeAssignments.put(classificationAttributeCode, assignment);
                }
                return assignment;
            }
        }
        return null;
    }

    /**
     * @return an unmodifiable list containing all the linked {@code ClassAttributeAssignmentModel}s
     */
    public List<ClassAttributeAssignmentModel> getClassAttributeAssignments() {
        return Collections.<ClassAttributeAssignmentModel> unmodifiableList(classAttributeAssignments);
    }

    /**
     * Check whether the specified assignment is not in the non empty assignments list.
     * 
     * @param assignment
     * @return {@code true} if the specified assignment is not in the non empty assignments list, {@code false} otherwise.
     */
    public boolean isEmpty(final ClassAttributeAssignmentModel assignment) {
        return assignment != null && //
                assignment.getClassificationAttribute() != null && //
                assignment.getClassificationAttribute().getCode() != null && //
                !nonEmptyClassAttributeAssignments.containsKey(assignment.getClassificationAttribute().getCode());
    }

    public ClassificationClassModel getClassificationClass() {
        return classificationClass;
    }
}

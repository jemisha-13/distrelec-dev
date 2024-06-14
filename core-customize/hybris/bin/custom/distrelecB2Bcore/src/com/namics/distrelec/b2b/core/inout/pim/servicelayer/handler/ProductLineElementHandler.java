/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Element handler for "Product" XMl elements with UserTypeID="Produktlinie". Such elements will result in models of type
 * {@link CategoryModel}. Since version v5.17, this handler is handling XML elements with UserTypeID="DL3_Productline" because of the
 * taxonomy changes.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class ProductLineElementHandler extends AbstractCategoryElementHandler {

    private static final Logger LOG = LogManager.getLogger(ProductLineElementHandler.class);

    @Autowired
    private ModelService modelService;

    @Autowired
    private ClassificationSystemService classificationSystemService;

    @Override
    protected void doOnStart(final Element element, final CategoryModel category) {
        getPimImportCategoryElementConverter().convertCategoryStructure(element, category, getImportContext());
        createClassificationClassIfNotExists(element);
    }

    private void createClassificationClassIfNotExists(final Element element) {
        ClassificationClassModel classificationClass = null;
        final String classificationClassCode = getClassificationClassCode(element);
        try {
            classificationClass = classificationSystemService.getClassForCode(getImportContext().getClassificationSystemVersion(), classificationClassCode);
        } catch (final UnknownIdentifierException e) {
            classificationClass = modelService.create(ClassificationClassModel.class);
            classificationClass.setCatalogVersion(getImportContext().getClassificationSystemVersion());
            classificationClass.setCode(classificationClassCode);
            classificationClass.setSupercategories(
                    Collections.<CategoryModel> singletonList(getImportContext().getRootClassificationClassWrapper().getClassificationClass()));
            modelService.save(classificationClass);
        }
        getImportContext().setCurrentClassificationClass(classificationClass);
    }

    private String getClassificationClassCode(final Element element) {
        return getImportContext().getClassificationClassCodePrefix() + getPimImportCategoryElementConverter().getId(element);
    }

    @Override
    protected void doOnEnd(final Element element, final CategoryModel category) {
        // Update classification class
        final ClassificationClassModel classificationClass = getImportContext().getCurrentClassificationClassWrapper().getClassificationClass();
        convert(category, classificationClass);
        try {
            modelService.save(classificationClass);
            // DISTRELEC-10680
            // checkEmptyClassAttributeAssignments();
        } catch (final ModelSavingException e) {
            LOG.error("Could not save ClassificationClass with code [" + classificationClass.getCode() + "]", e);
        } finally {
            modelService.detach(classificationClass);
        }
    }

    /**
     * Check the empty {@code ClassAttributeAssignmentModel}s from the list of {@code ClassAttributeAssignments} in the current
     * classification class. If one {@code ClassAttributeAssignmentModel} is not empty but flagged as empty, then the flag is set to
     * {@code false}.
     */
    protected void checkEmptyClassAttributeAssignments() {
        for (final ClassAttributeAssignmentModel assignment : getImportContext().getCurrentClassificationClassWrapper().getClassAttributeAssignments()) {
            final boolean empty = getImportContext().getCurrentClassificationClassWrapper().isEmpty(assignment);
            // If the two values are different, then we should update the database with the value of "empty"
            if (empty ^ assignment.isCaaEmpty()) {
                assignment.setCaaEmpty(empty);
                modelService.save(assignment);
                LOG.info(empty ? "Found new empty Class Attribute Assignment: {}" : "Found non empty Class Attribute Assignment which was flagged as empty: {}",
                        assignment.getClassificationAttribute().getCode());
            }
        }
    }

    private void convert(final CategoryModel category, final ClassificationClassModel classificationClass) {
        classificationClass.setCategories(Collections.singletonList(category));
        classificationClass.setPrimaryImage(category.getPrimaryImage());
        classificationClass.setAllowedPrincipals(category.getAllowedPrincipals());
        classificationClass.setName(category.getName());
    }

}

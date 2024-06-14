/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.ElementPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.PimImportElementConverter;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.ElementConverterException;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Element handler for "AttributeLink" XML elements. Such elements will result in models of type {@link ClassAttributeAssignmentModel}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class AttributeLinkElementHandler extends AbstractPimImportElementHandler {

    private static final Logger LOG = LogManager.getLogger(AttributeLinkElementHandler.class);

    @Autowired
    private ClassificationSystemService classificationSystemService;

    @Autowired
    private ModelService modelService;

    private PimImportElementConverter attributeLinkElementConverter;

    @Override
    public void onEnd(final ElementPath elementPath) {
        final Element element = elementPath.getCurrent();
        try {
            doUpdateIfRequired(element);
        } finally {
            getImportContext().incrementCounter(ClassAttributeAssignmentModel._TYPECODE);
            element.detach();
        }
    }

    private void doUpdateIfRequired(final Element element) {
        if (getImportContext().getCurrentClassificationClassWrapper() == null) {
            LOG.debug("Skip processing AttributeLink because it's not in a Product-Line");
        } else {
            doUpdate(element);
        }
    }

    private void doUpdate(final Element element) {
        final String attributeCode = attributeLinkElementConverter.getId(element);

        ClassAttributeAssignmentModel assignment = null;

        try {
            assignment = getModel(attributeCode);
            attributeLinkElementConverter.convert(element, assignment, getImportContext(), null);
            modelService.save(assignment);
        } catch (final ElementConverterException e) {
            LOG.error("Could not convert AttributeLink element with attributeCode [" + attributeCode + "] within classification class ["
                    + assignment.getClassificationClass().getCode() + "]: " + e.getMessage());
        } catch (final ModelSavingException e) {
            LOG.error("Could not save ClassAttributeAssignment with attributeCode [" + attributeCode + "] within classification class ["
                    + assignment.getClassificationClass().getCode() + "]", e);
        } catch (final UnknownIdentifierException e) {
            LOG.error("Could not create ClassAttributeAssignment with attributeCode [" + attributeCode + "] within classification class [" + assignment + "]",
                    e);
        } finally {
            if (assignment != null) {
                modelService.detach(assignment);
            }
        }
    }

    private ClassAttributeAssignmentModel getModel(final String attributeCode) {
        ClassAttributeAssignmentModel assignment = getImportContext().getCurrentClassificationClassWrapper().getClassAttributeAssignment(attributeCode);

        if (assignment == null) {
            assignment = modelService.create(ClassAttributeAssignmentModel.class);
            assignment.setClassificationClass(getImportContext().getCurrentClassificationClassWrapper().getClassificationClass());
            assignment.setClassificationAttribute(
                    classificationSystemService.getAttributeForCode(getImportContext().getClassificationSystemVersion(), attributeCode));

            /*
             * Add assignment to temporary list (ProductElementConverter uses this list to get the assignment). Temporary list is used
             * instead ClassificationClass.declaredClassificationAttributeAssignments to prevent hybris from overwrite assignment position.
             */
            getImportContext().getCurrentClassificationClassWrapper().addClassAttributeAssignment(assignment);
        }

        return assignment;
    }

    public PimImportElementConverter getAttributeLinkElementConverter() {
        return attributeLinkElementConverter;
    }

    @Required
    public void setAttributeLinkElementConverter(final PimImportElementConverter attributeLinkElementConverter) {
        this.attributeLinkElementConverter = attributeLinkElementConverter;
    }

}
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
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.MasterImportModelNotFoundException;

import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Element handler for "Attribute" XML elements. Such elements will result in models of type {@link ClassificationAttributeModel}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class AttributeElementHandler extends AbstractPimImportElementHandler {

    private static final Logger LOG = LogManager.getLogger(AttributeElementHandler.class);

    @Autowired
    private ClassificationSystemService classificationSystemService;

    @Autowired
    private ModelService modelService;

    private PimImportElementConverter attributeElementConverter;

    @Override
    public void onEnd(final ElementPath elementPath) {
        final Element element = elementPath.getCurrent();
        try {
            doUpdate(element);
        } finally {
            getImportContext().incrementCounter(ClassificationAttributeModel._TYPECODE);
            element.detach();
        }
    }

    private void doUpdate(final Element element) {
        final String attributeCode = attributeElementConverter.getId(element);
        ClassificationAttributeModel model = null;

        try {
            model = getModel(attributeCode);
        } catch (final MasterImportModelNotFoundException e) {
            LOG.error("Model for typeCode [" + ClassificationAttributeModel._TYPECODE + "] and ID [" + attributeCode + "] not found", e);
            return;
        }

        try {
            attributeElementConverter.convert(element, model, getImportContext(), null);
            modelService.save(model);
        } catch (final ElementConverterException e) {
            LOG.error("Could not convert Attribute element with attributeCode [" + attributeCode + "]: " + e.getMessage());
        } catch (final ModelSavingException e) {
            LOG.error("Could not save ClassificationAttribute with code [" + model.getCode() + "]", e);
        } finally {
            modelService.detach(model);
        }
    }

    protected ClassificationAttributeModel getModel(final String attributeCode) {
        ClassificationAttributeModel attribute = null;
        try {
            attribute = classificationSystemService.getAttributeForCode(getImportContext().getClassificationSystemVersion(), attributeCode);
        } catch (final UnknownIdentifierException e) {
            attribute = modelService.create(ClassificationAttributeModel.class);
            attribute.setSystemVersion(getImportContext().getClassificationSystemVersion());
            attribute.setCode(attributeCode);
        }
        return attribute;
    }

    public PimImportElementConverter getAttributeElementConverter() {
        return attributeElementConverter;
    }

    @Required
    public void setAttributeElementConverter(final PimImportElementConverter attributeElementConverter) {
        this.attributeElementConverter = attributeElementConverter;
    }

}

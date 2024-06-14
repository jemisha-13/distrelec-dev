/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Element handler for "Unit" XML elements. Such elements will result in models of type {@link ClassificationAttributeUnitModel}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class UnitElementHandler extends AbstractHashAwarePimImportElementHandler {

    @Autowired
    private ClassificationSystemService classificationSystemService;

    public UnitElementHandler() {
        super(ClassificationAttributeUnitModel._TYPECODE);
    }

    @Override
    protected ItemModel getModel(final String code, final Element element) {
        try {
            return classificationSystemService.getAttributeUnitForCode(getImportContext().getClassificationSystemVersion(), code);
        } catch (final UnknownIdentifierException e) {
            final ClassificationAttributeUnitModel unitModel = getModelService().create(ClassificationAttributeUnitModel.class);
            unitModel.setSystemVersion(getImportContext().getClassificationSystemVersion());
            unitModel.setCode(code);
            return unitModel;
        }
    }

}

/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.classification;

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.classification.ClassAttributeAssignment;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttribute;
import de.hybris.platform.catalog.jalo.classification.ClassificationClass;
import de.hybris.platform.catalog.jalo.classification.ClassificationSystemVersion;
import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.AbstractImpExCSVCellDecorator;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.jalo.JaloSystemException;

import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * WARNING: not finished yet!!!
 * 
 * @author pnueesch, namics ag
 * @since Distrelec 1.0
 * 
 */
public class ProductFeatureAttributeLookupDecorator extends AbstractImpExCSVCellDecorator {

    private static final String CLASSIFICATION_SYSTEM_ID = "classificationSystemId";
    private static final String CLASSIFICATION_SYSTEM_VERSION = "classificationSystemVersion";
    private static final String ATTRIBUTE_INFORMATION_INDEX = "attributeInformationIndex";

    private static final Integer CLASSIFICATION_CLASS_CODE_INDEX = 0;
    private static final Integer CLASSIFICATION_ATTRIBUTE_INDEX = 3;

    private String classificationSystemId = "";
    private String classificationSystemVersion = "";
    private Integer attributeInformationIndex = 0;
    private String attributeSeparator = ":";

    private ClassificationSystemVersion classificationSystem;

    @Override
    public void init(final AbstractColumnDescriptor column) throws HeaderValidationException {
        super.init(column);

        classificationSystemId = column.getDescriptorData().getModifier(CLASSIFICATION_SYSTEM_ID);
        classificationSystemVersion = column.getDescriptorData().getModifier(CLASSIFICATION_SYSTEM_VERSION);
        attributeInformationIndex = Integer.valueOf(column.getDescriptorData().getModifier(ATTRIBUTE_INFORMATION_INDEX));

        if (!StringUtils.hasText(classificationSystemId)) {
            throw new JaloSystemException("Neccessary attribute 'classificationSystemId' not found in header definition!");
        }
        if (!StringUtils.hasText(classificationSystemVersion)) {
            throw new JaloSystemException("Neccessary attribute 'classificationSystemVersion' not found in header definition!");
        }
        if (attributeInformationIndex == null || attributeInformationIndex < 0) {
            throw new JaloSystemException("Neccessary attribute 'attributeInformationIndex' not found in header definition!");
        }
        // TODO pnueesch: Check if index isn't too big

        classificationSystem = CatalogManager.getInstance().getClassificationSystem(classificationSystemId).getSystemVersion(classificationSystemVersion);
    }

    @Override
    public String decorate(final int paramInt, final Map<Integer, String> paramMap) {
        final String attributeInformation = paramMap.get(attributeInformationIndex);
        final String[] attributeInformationArray = attributeInformation.split(attributeSeparator);
        if (attributeInformationArray.length <= CLASSIFICATION_ATTRIBUTE_INDEX) {
            throw new JaloSystemException("Expected at least " + (CLASSIFICATION_ATTRIBUTE_INDEX + 1) + " entries, got " + attributeInformationArray.length);
        }

        final ClassificationAttribute ca = classificationSystem.getClassificationAttribute(attributeInformationArray[CLASSIFICATION_ATTRIBUTE_INDEX]);
        // TODO pnueesch: error happens -> could not translate value expression 'session.catalogversions'
        final ClassificationClass cc = classificationSystem.getClassificationClass(attributeInformationArray[CLASSIFICATION_CLASS_CODE_INDEX]);
        final ClassAttributeAssignment caa = cc.getAttributeAssignment(ca);

        return caa.getAttributeType().getCode() + "," + paramMap.get(paramInt);
    }
}

/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.classification;

import java.util.Map;

import org.apache.log4j.Logger;

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.classification.ClassificationSystemVersion;
import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.AbstractImpExCSVCellDecorator;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;

/**
 * <p>
 * Replace a String like 'MEL_M-Garantie_2Jahre(MEL_M-Garantie_2Jahre(8796193882720))' to 'MEL_M-Garantie_2Jahre' if a suitable
 * ClassificationAttributeValue exists.
 * 
 * In addition this decorator try to avoid a composition like 'number,text'
 * </p>
 * 
 * 
 * @author mwegener, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class ProductFeatureValueDecorator extends AbstractImpExCSVCellDecorator {
    private static final Logger LOG = Logger.getLogger(ProductFeatureValueDecorator.class.getName());

    public static final String CLASSIFICATION_SYSTEM_NAME_MODIFIER = "classificationSystem";
    public static final String CLASSIFICATION_SYSTEM_VERSION_NAME_MODIFIER = "classificationSystemVersion";
    public static final String COLLECTION_DELIMITER = "collection-delimiter";

    private String classificationSystem = "";
    private String classificationSystemVersion = "";
    private String collectionDelimiter = ",";

    @Override
    public void init(final AbstractColumnDescriptor column) throws HeaderValidationException {
        super.init(column);

        classificationSystem = column.getDescriptorData().getModifier(CLASSIFICATION_SYSTEM_NAME_MODIFIER);
        classificationSystemVersion = column.getDescriptorData().getModifier(CLASSIFICATION_SYSTEM_VERSION_NAME_MODIFIER);
        collectionDelimiter = column.getDescriptorData().getModifier(COLLECTION_DELIMITER);

        if (classificationSystem == null) {
            classificationSystem = "";
        }

        if (classificationSystemVersion == null) {
            classificationSystemVersion = "";
        }

        if ("".equals(classificationSystem)) {
            throw new HeaderValidationException("The modifier [" + CLASSIFICATION_SYSTEM_NAME_MODIFIER + "] in " + ProductFeatureValueDecorator.class
                    + " must be set.", -1);
        }

        if ("".equals(classificationSystemVersion)) {
            throw new HeaderValidationException("The modifier [" + CLASSIFICATION_SYSTEM_VERSION_NAME_MODIFIER + "] in " + ProductFeatureValueDecorator.class
                    + " must be set.", -1);
        }

    }

    @Override
    public String decorate(final int paramInt, final Map<Integer, String> paramMap) {
        String parsedValue = null;
        String resultValue = paramMap.get(Integer.valueOf(paramInt));
        String importValue[] = paramMap.get(Integer.valueOf(paramInt)).split(collectionDelimiter);

        try {
            // verify data type
            if (importValue[0].equals("enum")) {
                parsedValue = importValue[1].substring(0, importValue[1].indexOf('('));
            } else if ("number".equals(importValue[0])) {
                Double.parseDouble(importValue[1]);
            } else if ("boolean".equals(importValue[0])) {
                Boolean.parseBoolean(importValue[1]);
            }

        } catch (Exception e) {
            importValue[0] = "string";
            resultValue = importValue[0] + collectionDelimiter + importValue[1];
        }

        final ClassificationSystemVersion classVersion = CatalogManager.getInstance().getClassificationSystem(classificationSystem)
                .getSystemVersion(classificationSystemVersion);

        if (classVersion == null) {
            throw new NullPointerException("ClassificationSystemVersion with [" + CLASSIFICATION_SYSTEM_NAME_MODIFIER + "] = '" + classificationSystem
                    + "' and [" + CLASSIFICATION_SYSTEM_VERSION_NAME_MODIFIER + "] = '" + classificationSystemVersion + "' does not exists.");
        }

        if ("enum".equals(importValue[0]) && classVersion.getClassificationAttributeValue(parsedValue) != null) {

            resultValue = importValue[0] + collectionDelimiter + classificationSystem + collectionDelimiter + classificationSystemVersion + collectionDelimiter
                    + parsedValue;
        }

        LOG.debug(resultValue);
        return resultValue;
    }
}

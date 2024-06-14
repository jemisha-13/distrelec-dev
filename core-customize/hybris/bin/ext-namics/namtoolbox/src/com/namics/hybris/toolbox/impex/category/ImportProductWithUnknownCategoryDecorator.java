/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.category;

import java.util.Map;

import org.apache.log4j.Logger;

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.AbstractImpExCSVCellDecorator;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;

/**
 * <p>
 * Checks a list of classes if all classes are known during the ImpEx Import. If a class is unknown, it will be removed from the list.
 * </p>
 * 
 * 
 * @author mwegener, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class ImportProductWithUnknownCategoryDecorator extends AbstractImpExCSVCellDecorator {
    private static final Logger LOG = Logger.getLogger(ImportProductWithUnknownCategoryDecorator.class.getName());

    public static final String CATALOG_NAME_MODIFIER = "catalogName";
    public static final String CATALOG_VERSION_NAME_MODIFIER = "catalogVersionName";
    public static final String LIST_DELIMITER_MODIFIER = "listDelimiter";

    private String catalogName = "";
    private String catalogVersionName = "";
    private String listDelimiter = ",";

    @Override
    public void init(final AbstractColumnDescriptor column) throws HeaderValidationException {
        super.init(column);

        catalogName = column.getDescriptorData().getModifier(CATALOG_NAME_MODIFIER);
        catalogVersionName = column.getDescriptorData().getModifier(CATALOG_VERSION_NAME_MODIFIER);
        listDelimiter = column.getDescriptorData().getModifier(LIST_DELIMITER_MODIFIER);

        if (catalogName == null) {
            catalogName = "";
        }

        if (catalogVersionName == null) {
            catalogVersionName = "";
        }

        if (listDelimiter == null) {
            listDelimiter = "";
        }

        if ("".equals(catalogName)) {
            throw new HeaderValidationException("The modifier [" + CATALOG_NAME_MODIFIER + "] in " + ImportProductWithUnknownCategoryDecorator.class
                    + " must be set.", -1);
        }

        if ("".equals(catalogVersionName)) {
            throw new HeaderValidationException("The modifier [" + CATALOG_VERSION_NAME_MODIFIER + "] in " + ImportProductWithUnknownCategoryDecorator.class
                    + " must be set.", -1);
        }

        if ("".equals(listDelimiter)) {
            throw new HeaderValidationException("The modifier [" + LIST_DELIMITER_MODIFIER + "] in " + ImportProductWithUnknownCategoryDecorator.class
                    + " must be set.", -1);
        }

    }

    @Override
    public String decorate(final int paramInt, final Map<Integer, String> paramMap) {
        final CatalogVersion catVersion = CatalogManager.getInstance().getCatalog(catalogName).getCatalogVersion(catalogVersionName);

        if (catVersion == null) {
            throw new NullPointerException("CatalogVersion with [" + CATALOG_NAME_MODIFIER + "] = '" + catalogName + "' and [" + CATALOG_VERSION_NAME_MODIFIER
                    + "] = '" + catalogVersionName + "' does not exists.");
        }

        final String[] categories = paramMap.get(Integer.valueOf(paramInt)).split(listDelimiter);
        final StringBuffer buffer = new StringBuffer();

        for (String category : categories) {
            if (catVersion.getCategory(category) != null) {
                buffer.append(category + listDelimiter);
            } else {
                LOG.debug(category + " was removed from list");
            }
        }

        // Remove last list delimiter
        if (buffer.length() != 0) {
            buffer.deleteCharAt(buffer.length() - 1);
        }

        final String resultValue = buffer.toString();

        LOG.debug(resultValue);
        return resultValue;
    }

}

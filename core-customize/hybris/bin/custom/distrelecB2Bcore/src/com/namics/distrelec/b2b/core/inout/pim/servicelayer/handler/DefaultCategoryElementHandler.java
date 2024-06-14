/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import org.dom4j.Element;

import de.hybris.platform.category.model.CategoryModel;

/**
 * Handler for a "classification" or a "product" XML element. These elements will result in hybris product category models.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DefaultCategoryElementHandler extends AbstractCategoryElementHandler {

    @Override
    protected void doOnStart(final Element element, final CategoryModel category) {
        getPimImportCategoryElementConverter().convertCategoryStructure(element, category, getImportContext());
    }

    @Override
    protected void doOnEnd(final Element element, final CategoryModel category) {
        // Nothing to do
    }

}
/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import org.dom4j.Element;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;

import de.hybris.platform.category.model.CategoryModel;

/**
 * Interface with methods to convert a category XML element.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public interface PimImportCategoryElementConverter extends PimImportElementConverter<CategoryModel> {

    void convertCategoryStructure(final Element source, final CategoryModel target, final ImportContext importContext);
    
    void setPimCategoryType(final Element source, final CategoryModel target);

}

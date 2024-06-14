/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.category.model;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.CategoryCountryModel;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * Dynamic attribute handler to return country specific seo text.
 * 
 * @author pnueesch, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class CategorySeoTextAttributeHandler extends AbstractDynamicAttributeHandler<String, CategoryModel> {

    @Autowired
    private DistCategoryService distCategoryService;

    @Override
    public String get(final CategoryModel category) {
        final CategoryCountryModel countrySpecificInformation = getDistCategoryService().getCountrySpecificCategoryInformation(category);
        if (countrySpecificInformation != null) {
            return countrySpecificInformation.getSeoText();
        }
        return null;
    }

    public DistCategoryService getDistCategoryService() {
        return distCategoryService;
    }

    public void setDistCategoryService(DistCategoryService distCategoryService) {
        this.distCategoryService = distCategoryService;
    }

}

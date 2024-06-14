/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.ElementConverterException;

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Sets the super category of a "Product Line" category with information of a "ClassificationReference" element.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class ClassificationReferenceElementConverter extends AbstractCategoryElementConverter {

    private static final Logger LOG = LogManager.getLogger(ClassificationReferenceElementConverter.class);

    private static final String ATTRIBUTE_CLASSIFICATION_ID = "ClassificationID";
    private static final String XP_METADATA_CLASSIFICATION = "MetaData/Value[@AttributeID='web_template01']";

    @Autowired
    private CategoryService categoryService;

    @Override
    public String getId(final Element element) {
        // Returns the ID of the parent element (enclosing "Product Line" category)
        // final Element productLineElement = element.getParent();
        return element.attributeValue(ATTRIBUTE_CLASSIFICATION_ID).replace(CATEGORY_ID_SUFFIX, "");
    }

    @Override
    public void convertCategoryStructure(final Element source, final CategoryModel target, final ImportContext importContext) {
        // Do nothing. Super category is set during convert().
    }

    @Override
    public void convert(final Element source, final CategoryModel target, final ImportContext importContext, final String hash) {
        CategoryModel superCategory = null;

        final String superCategoryCode = getSuperCategoryCode(source, importContext);
        if (StringUtils.isNotBlank(superCategoryCode)) {
            try {
                superCategory = categoryService.getCategoryForCode(importContext.getProductCatalogVersion(), superCategoryCode);
            } catch (final UnknownIdentifierException e) {
                throw new ElementConverterException("Could not find category for code [" + superCategoryCode + "]", e);
            }
        }

        if (superCategory == null) {
            LOG.error("Could not find super category for element [{}]", source.asXML());
        } else {
            final List<CategoryModel> superCategories = (target.getSupercategories() != null ? new ArrayList<CategoryModel>(target.getSupercategories())
                                                                                             : new ArrayList<CategoryModel>());

            superCategories.add(superCategory);
            target.setSupercategories(superCategories);
        }

        setCategoryLevel(target, superCategory, importContext);
    }

    protected String getSuperCategoryCode(final Element classificationReferenceElement, final ImportContext importContext) {
        final String classificationId = classificationReferenceElement.attributeValue(ATTRIBUTE_CLASSIFICATION_ID).replace(CATEGORY_ID_SUFFIX, "");
        return importContext.getCategoryCodePrefix() + classificationId;
    }

}

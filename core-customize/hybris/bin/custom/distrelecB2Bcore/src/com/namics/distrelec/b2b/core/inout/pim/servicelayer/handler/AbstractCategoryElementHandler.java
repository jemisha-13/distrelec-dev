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

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.PimImportCategoryElementConverter;

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Abstract element handler for different category elements.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public abstract class AbstractCategoryElementHandler extends AbstractPimImportElementHandler {

    private static final Logger LOG = LogManager.getLogger(AbstractCategoryElementHandler.class);

    @Autowired
    private ModelService modelService;

    @Autowired
    private CategoryService categoryService;

    private PimImportCategoryElementConverter pimImportCategoryElementConverter;

    @Override
    public void onPimImportElementStart(final ElementPath elementPath) {
        final Element element = elementPath.getCurrent();
        createIfNotExists(element);
    }

    private void createIfNotExists(final Element element) {
        CategoryModel category = null;
        final String categoryCode = getCategoryCode(element);
        try {
            category = categoryService.getCategoryForCode(categoryCode);
        } catch (final UnknownIdentifierException e) {
            category = modelService.create(CategoryModel.class);
            category.setCatalogVersion(getImportContext().getProductCatalogVersion());
            category.setCode(categoryCode);
            modelService.save(category);
        }

        if (category != null) {
            getImportContext().getCategoryCache().put(categoryCode, category);
            doOnStart(element, category);
        } else {
            LOG.error("Category not found '" + categoryCode + "' in import");
        }
    }

    protected abstract void doOnStart(final Element element, final CategoryModel category);

    @Override
    public void onEnd(final ElementPath elementPath) {
        final Element element = elementPath.getCurrent();
        try {
            doUpdate(element);
        } finally {
            getImportContext().incrementCounter(CategoryModel._TYPECODE);
            element.detach();
        }
    }

    protected void doUpdate(final Element element) {
        final String categoryCode = getCategoryCode(element);
        // Get from memory and clean up
        final CategoryModel category = getImportContext().getCategoryCache().remove(categoryCode);
        if (category != null) {
            getPimImportCategoryElementConverter().convert(element, category, getImportContext(), null);
            // TODO: SET PIM CATEGORY TYPE --> AbstractCategoryElementConverter.setPIMCategoryType
            getPimImportCategoryElementConverter().setPimCategoryType(element, category);
            try {
                modelService.save(category);
            } catch (final ModelSavingException e) {
                LOG.error("Could not save Category with code [" + category.getCode() + "]", e);
            } finally {
                modelService.detach(category);
            }

            doOnEnd(element, category);
        }
    }

    protected abstract void doOnEnd(final Element element, final CategoryModel category);

    protected String getCategoryCode(final Element element) {
        return getImportContext().getCategoryCodePrefix() + pimImportCategoryElementConverter.getId(element);
    }

    public PimImportCategoryElementConverter getPimImportCategoryElementConverter() {
        return pimImportCategoryElementConverter;
    }

    @Required
    public void setPimImportCategoryElementConverter(final PimImportCategoryElementConverter pimImportCategoryElementConverter) {
        this.pimImportCategoryElementConverter = pimImportCategoryElementConverter;
    }
}

/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.ElementPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.PimImportElementConverter;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Element handler for "ClassificationReference" XML elements. Such elements are used to set the super category of a "Product Line"
 * category.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class ClassificationReferenceElementHandler extends AbstractPimImportElementHandler {

    private static final Logger LOG = LogManager.getLogger(ClassificationReferenceElementHandler.class);

    private PimImportElementConverter classificationReferenceElementConverter;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ClassificationSystemService classificationSystemService;

    @Autowired
    private ModelService modelService;

    @Override
    public void onEnd(final ElementPath elementPath) {
        final Element element = elementPath.getCurrent();
        try {
            final CategoryModel category = getCategory(element, getImportContext());
            if (category == null) {
                LOG.error("Could not find Category for ClassificationReference Element [{}]", element.asXML());
                getImportContext().setImportProductsOfCurrentProductLine(false);
            } else {
                // classificationReferenceElementConverter.convert(element, category, getImportContext(), null);
                getImportContext().setImportProductsOfCurrentProductLine(isWhitelistedCategory(category, getImportContext().getWhitelistedCategories()));
                setClassificationClass(element);
            }
        }
        catch(Exception e)
        {
            LOG.error("error getting category:"+element,e);
        }
        finally {
            // element.detach();
        }
    }

    /**
     * In the new taxonomy structure, we have the product lines in the Classification XML elements and not in the Products anymore. We need
     * to set the current classification class to the correct one from the ClassificationReference sub-element. The change of the current
     * classification class is done only if the current product does not belong to the existing one.
     * 
     * <h4>Example:</h4>
     * 
     * <pre>
     * <ClassificationReference ClassificationID="DNAV_PL_01010103" Type="e-ShopDistrelec" AnalyzerResult="included"/>
     * </pre>
     * 
     * @param classificationReferenceElement
     *            the ClassificationReference XML element to be processed
     */
    private void setClassificationClass(final Element classificationReferenceElement) {
        ClassificationClassModel classificationClass = getImportContext().getCurrentClassificationClassWrapper() != null
                ? getImportContext().getCurrentClassificationClassWrapper().getClassificationClass() : null;
        final String classificationId = getImportContext().getClassificationClassCodePrefix()
                + classificationReferenceElementConverter.getId(classificationReferenceElement);

        // Check first if we need to update or not
        if (classificationClass != null && StringUtils.equals(classificationId, classificationClass.getCode())) {
            return;
        }

        // We first process the empty class attribute assignments from the previous product line.
        checkEmptyClassAttributeAssignments();

        // Then we update the current ClassificationClass
        try {
            classificationClass = classificationSystemService.getClassForCode(getImportContext().getClassificationSystemVersion(), classificationId);
            getImportContext().setCurrentClassificationClass(classificationClass);
        } catch (final UnknownIdentifierException e) {
            LOG.error("Could not find the ClassificationClass with code [{}]", classificationId);
        }
    }

    /**
     * Check the empty {@code ClassAttributeAssignmentModel}s from the list of {@code ClassAttributeAssignments} in the current
     * classification class. If one {@code ClassAttributeAssignmentModel} is not empty but flagged as empty, then the flag is set to
     * {@code false}.
     */
    private void checkEmptyClassAttributeAssignments() {
        if (getImportContext().getCurrentClassificationClassWrapper() == null //
                || CollectionUtils.isEmpty(getImportContext().getCurrentClassificationClassWrapper().getClassAttributeAssignments())) {
            return;
        }
        for (final ClassAttributeAssignmentModel assignment : getImportContext().getCurrentClassificationClassWrapper().getClassAttributeAssignments()) {
            final boolean empty = getImportContext().getCurrentClassificationClassWrapper().isEmpty(assignment);
            // If the two values are different, then we should update the database with the value of "empty"
            if (empty ^ assignment.isCaaEmpty()) {
                assignment.setCaaEmpty(empty);
                modelService.save(assignment);
                LOG.info(empty ? "Found new empty Class Attribute Assignment: {}" : "Found non empty Class Attribute Assignment which was flagged as empty: {}",
                        assignment.getClassificationAttribute().getCode());
            }
        }
    }

    private boolean isWhitelistedCategory(final CategoryModel category, final Set<String> whitelistedCategories) {
        if (whitelistedCategories.isEmpty()) {
            return true;
        }
        for (CategoryModel superCategory : category.getAllSupercategories()) {
            if (whitelistedCategories.contains(superCategory.getCode())) {
                return true;
            }
        }
        LOG.info("Category [{}] is not whitelisted. Containing products will not be imported.", category.getCode());
        return false;
    }

    private CategoryModel getCategory(final Element classificationReferenceElement, final ImportContext importContext) {
        final String categoryCode = getCategoryCode(classificationReferenceElement);

        return importContext.getCategoryCache().get(categoryCode) == null ? categoryService.getCategoryForCode(categoryCode)
                : importContext.getCategoryCache().get(categoryCode);
    }

    private String getCategoryCode(final Element classificationReferenceElement) {
        return getImportContext().getCategoryCodePrefix() + classificationReferenceElementConverter.getId(classificationReferenceElement);
    }

    public PimImportElementConverter getClassificationReferenceElementConverter() {
        return classificationReferenceElementConverter;
    }

    @Required
    public void setClassificationReferenceElementConverter(final PimImportElementConverter classificationReferenceElementConverter) {
        this.classificationReferenceElementConverter = classificationReferenceElementConverter;
    }

    public CategoryService getCategoryService() {
        return categoryService;
    }

    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public ClassificationSystemService getClassificationSystemService() {
        return classificationSystemService;
    }

    public void setClassificationSystemService(final ClassificationSystemService classificationSystemService) {
        this.classificationSystemService = classificationSystemService;
    }
}
